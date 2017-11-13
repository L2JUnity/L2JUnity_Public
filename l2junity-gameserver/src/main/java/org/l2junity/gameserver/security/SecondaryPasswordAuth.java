/*
 * Copyright (C) 2004-2015 L2J Unity
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
package org.l2junity.gameserver.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.l2junity.gameserver.LoginServerThread;
import org.l2junity.gameserver.data.xml.impl.SecondaryAuthData;
import org.l2junity.gameserver.model.variables.AccountVariables;
import org.l2junity.gameserver.network.client.Disconnection;
import org.l2junity.gameserver.network.client.L2GameClient;
import org.l2junity.gameserver.network.client.send.Ex2ndPasswordAck;
import org.l2junity.gameserver.network.client.send.Ex2ndPasswordCheck;
import org.l2junity.gameserver.network.client.send.Ex2ndPasswordVerify;
import org.l2junity.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mrTJO, UnAfraid
 */
public class SecondaryPasswordAuth
{
	private final Logger LOGGER = LoggerFactory.getLogger(SecondaryPasswordAuth.class);
	private static final String VAR_PWD = "secauth_pwd";
	private static final String VAR_WTE = "secauth_wte";
	
	private final L2GameClient _activeClient;
	private final AccountVariables _vars;
	private boolean _authed;
	
	public SecondaryPasswordAuth(L2GameClient activeClient)
	{
		_activeClient = activeClient;
		_vars = new AccountVariables(_activeClient.getAccountName());
	}
	
	public boolean savePassword(String password)
	{
		if (passwordExist())
		{
			LOGGER.warn("{} forced savePassword", _activeClient.getAccountName());
			Disconnection.of(_activeClient).defaultSequence(false);
			return false;
		}
		
		if (!validatePassword(password))
		{
			_activeClient.sendPacket(new Ex2ndPasswordAck(0, Ex2ndPasswordAck.WRONG_PATTERN));
			return false;
		}
		
		password = cryptPassword(password);
		
		_vars.set(VAR_PWD, password);
		_vars.storeMe();
		return true;
	}
	
	public boolean insertWrongAttempt(int attempts)
	{
		_vars.set(VAR_WTE, attempts);
		_vars.storeMe();
		return true;
	}
	
	public boolean changePassword(String oldPassword, String newPassword)
	{
		if (!passwordExist())
		{
			LOGGER.warn("{} forced changePassword", _activeClient.getAccountName());
			Disconnection.of(_activeClient).defaultSequence(false);
			return false;
		}
		
		if (!checkPassword(oldPassword, true))
		{
			return false;
		}
		
		if (!validatePassword(newPassword))
		{
			_activeClient.sendPacket(new Ex2ndPasswordAck(2, Ex2ndPasswordAck.WRONG_PATTERN));
			return false;
		}
		
		newPassword = cryptPassword(newPassword);
		
		_vars.set(VAR_PWD, newPassword);
		_vars.storeMe();
		_authed = false;
		return true;
	}
	
	public boolean checkPassword(String password, boolean skipAuth)
	{
		password = cryptPassword(password);
		
		final String currentPassword = _vars.getString(VAR_PWD, null);
		int wrongAttempts = _vars.getInt(VAR_WTE, 0);
		if (!password.equals(currentPassword))
		{
			wrongAttempts++;
			if (wrongAttempts < SecondaryAuthData.getInstance().getMaxAttempts())
			{
				_activeClient.sendPacket(new Ex2ndPasswordVerify(Ex2ndPasswordVerify.PASSWORD_WRONG, wrongAttempts));
				insertWrongAttempt(wrongAttempts);
			}
			else
			{
				LoginServerThread.getInstance().sendTempBan(_activeClient.getAccountName(), _activeClient.getIP(), SecondaryAuthData.getInstance().getBanTime());
				LoginServerThread.getInstance().sendMail(_activeClient.getAccountName(), "SATempBan", _activeClient.getIP(), Integer.toString(SecondaryAuthData.getInstance().getMaxAttempts()), Long.toString(SecondaryAuthData.getInstance().getBanTime()), SecondaryAuthData.getInstance().getRecoveryLink());
				LOGGER.warn("{} - ({}) has inputted the wrong password {} times in row.", _activeClient.getAccountName(), _activeClient.getIP(), wrongAttempts);
				insertWrongAttempt(0);
				_activeClient.close(new Ex2ndPasswordVerify(Ex2ndPasswordVerify.PASSWORD_BAN, SecondaryAuthData.getInstance().getMaxAttempts()));
			}
			return false;
		}
		if (!skipAuth)
		{
			_authed = true;
			_activeClient.sendPacket(new Ex2ndPasswordVerify(Ex2ndPasswordVerify.PASSWORD_OK, wrongAttempts));
		}
		insertWrongAttempt(0);
		return true;
	}
	
	public boolean passwordExist()
	{
		final String password = _vars.getString(VAR_PWD, null);
		return (password != null) && !password.isEmpty();
	}
	
	public void openDialog()
	{
		if (passwordExist())
		{
			_activeClient.sendPacket(new Ex2ndPasswordCheck(Ex2ndPasswordCheck.PASSWORD_PROMPT));
		}
		else
		{
			_activeClient.sendPacket(new Ex2ndPasswordCheck(Ex2ndPasswordCheck.PASSWORD_NEW));
		}
	}
	
	public boolean isAuthed()
	{
		return _authed;
	}
	
	private String cryptPassword(String password)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] raw = password.getBytes("UTF-8");
			byte[] hash = md.digest(raw);
			return Base64.getEncoder().encodeToString(hash);
		}
		catch (NoSuchAlgorithmException e)
		{
			LOGGER.error("Unsupported Algorythm", e);
		}
		catch (UnsupportedEncodingException e)
		{
			LOGGER.error("Unsupported Encoding", e);
		}
		return null;
	}
	
	private boolean validatePassword(String password)
	{
		if (!Util.isDigit(password))
		{
			return false;
		}
		
		if ((password.length() < 6) || (password.length() > 8))
		{
			return false;
		}
		
		return !SecondaryAuthData.getInstance().isForbiddenPassword(password);
	}
}