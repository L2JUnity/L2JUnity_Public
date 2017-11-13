/*
 * Copyright (C) 2004-2017 L2J Unity
 * 
 * This file is part of L2J Unity.
 * 
 * L2J Unity is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Unity is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.commons.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.l2junity.commons.util.Base32.DecodingException;

/**
 * Based on https://github.com/wstrange/GoogleAuth
 * @author UnAfraid
 */
public class GoogleAuthenticator
{
	private static final int CODE_DIGITS = 6;
	private static final int WINDOW_SIZE = 3;
	private static final long TIME_WINDOW = TimeUnit.SECONDS.toMillis(30);
	
	private static final int KEY_MODULUS = (int) Math.pow(10, CODE_DIGITS);
	
	private GoogleAuthenticator()
	{
		// Visibility
	}
	
	/**
	 * Cryptographic hash function used to calculate the HMAC (Hash-based Message Authentication Code). This implementation uses the SHA1 hash function.
	 */
	private static final String HMAC_HASH_FUNCTION = "HmacSHA1";
	
	public static boolean authorize(String secret, int verificationCode) throws DecodingException, InvalidKeyException, NoSuchAlgorithmException
	{
		return authorize(secret, verificationCode, System.currentTimeMillis());
	}
	
	public static boolean authorize(String secret, int verificationCode, long time) throws DecodingException, InvalidKeyException, NoSuchAlgorithmException
	{
		// Checking user input and failing if the secret key was not provided.
		if (secret == null)
		{
			throw new IllegalArgumentException("Secret cannot be null.");
		}
		
		// Checking if the verification code is between the legal bounds.
		if ((verificationCode <= 0) || (verificationCode >= KEY_MODULUS))
		{
			return false;
		}
		
		// Checking the validation code using the current UNIX time.
		return checkCode(secret, verificationCode, time, WINDOW_SIZE);
	}
	
	private static boolean checkCode(String secret, long code, long timestamp, int window) throws DecodingException, InvalidKeyException, NoSuchAlgorithmException
	{
		final byte[] decodedKey = Base32.decode(secret);
		
		// convert unix time into a 30 second "window" as specified by the
		// TOTP specification. Using Google's default interval of 30 seconds.
		final long timeWindow = timestamp / TIME_WINDOW;
		
		// Calculating the verification code of the given key in each of the
		// time intervals and returning true if the provided code is equal to
		// one of them.
		for (int i = -((window - 1) / 2); i <= (window / 2); ++i)
		{
			// Calculating the verification code for the current time interval.
			long hash = calculateCode(decodedKey, timeWindow + i);
			
			// Checking if the provided code is equal to the calculated one.
			if (hash == code)
			{
				// The verification code is valid.
				return true;
			}
		}
		
		// The verification code is invalid.
		return false;
	}
	
	private static int calculateCode(byte[] key, long tm) throws NoSuchAlgorithmException, InvalidKeyException
	{
		// Allocating an array of bytes to represent the specified instant
		// of time.
		final byte[] data = new byte[8];
		long value = tm;
		
		// Converting the instant of time from the long representation to a
		// big-endian array of bytes (RFC4226, 5.2. Description).
		for (int i = 8; i-- > 0; value >>>= 8)
		{
			data[i] = (byte) value;
		}
		
		// Building the secret key specification for the HmacSHA1 algorithm.
		final SecretKeySpec signKey = new SecretKeySpec(key, HMAC_HASH_FUNCTION);
		
		// Getting an HmacSHA1 algorithm implementation from the JCE.
		final Mac mac = Mac.getInstance(HMAC_HASH_FUNCTION);
		
		// Initializing the MAC algorithm.
		mac.init(signKey);
		
		// Processing the instant of time and getting the encrypted data.
		final byte[] hash = mac.doFinal(data);
		
		// Building the validation code performing dynamic truncation
		// (RFC4226, 5.3. Generating an HOTP value)
		final int offset = hash[hash.length - 1] & 0xF;
		
		// We are using a long because Java hasn't got an unsigned integer type
		// and we need 32 unsigned bits).
		long truncatedHash = 0;
		
		for (int i = 0; i < 4; ++i)
		{
			truncatedHash <<= 8;
			
			// Java bytes are signed but we need an unsigned integer:
			// cleaning off all but the LSB.
			truncatedHash |= (hash[offset + i] & 0xFF);
		}
		
		// Clean bits higher than the 32nd (inclusive) and calculate the
		// module with the maximum validation code value.
		truncatedHash &= 0x7FFFFFFF;
		truncatedHash %= KEY_MODULUS;
		
		// Returning the validation code to the caller.
		return (int) truncatedHash;
	}
}
