/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2junity.scripts.quests;

import org.l2junity.gameserver.scripting.annotations.GameScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.l2junity.scripts.quests.Q00013_ParcelDelivery.Q00013_ParcelDelivery;
import org.l2junity.scripts.quests.Q00015_SweetWhispers.Q00015_SweetWhispers;
import org.l2junity.scripts.quests.Q00016_TheComingDarkness.Q00016_TheComingDarkness;
import org.l2junity.scripts.quests.Q00017_LightAndDarkness.Q00017_LightAndDarkness;
import org.l2junity.scripts.quests.Q00019_GoToThePastureland.Q00019_GoToThePastureland;
import org.l2junity.scripts.quests.Q00020_BringUpWithLove.Q00020_BringUpWithLove;
import org.l2junity.scripts.quests.Q00031_SecretBuriedInTheSwamp.Q00031_SecretBuriedInTheSwamp;
import org.l2junity.scripts.quests.Q00032_AnObviousLie.Q00032_AnObviousLie;
import org.l2junity.scripts.quests.Q00033_MakeAPairOfDressShoes.Q00033_MakeAPairOfDressShoes;
import org.l2junity.scripts.quests.Q00034_InSearchOfCloth.Q00034_InSearchOfCloth;
import org.l2junity.scripts.quests.Q00035_FindGlitteringJewelry.Q00035_FindGlitteringJewelry;
import org.l2junity.scripts.quests.Q00036_MakeASewingKit.Q00036_MakeASewingKit;
import org.l2junity.scripts.quests.Q00037_MakeFormalWear.Q00037_MakeFormalWear;
import org.l2junity.scripts.quests.Q00040_ASpecialOrder.Q00040_ASpecialOrder;
import org.l2junity.scripts.quests.Q00042_HelpTheUncle.Q00042_HelpTheUncle;
import org.l2junity.scripts.quests.Q00043_HelpTheSister.Q00043_HelpTheSister;
import org.l2junity.scripts.quests.Q00044_HelpTheSon.Q00044_HelpTheSon;
import org.l2junity.scripts.quests.Q00061_LawEnforcement.Q00061_LawEnforcement;
import org.l2junity.scripts.quests.Q00109_InSearchOfTheNest.Q00109_InSearchOfTheNest;
import org.l2junity.scripts.quests.Q00110_ToThePrimevalIsle.Q00110_ToThePrimevalIsle;
import org.l2junity.scripts.quests.Q00111_ElrokianHuntersProof.Q00111_ElrokianHuntersProof;
import org.l2junity.scripts.quests.Q00115_TheOtherSideOfTruth.Q00115_TheOtherSideOfTruth;
import org.l2junity.scripts.quests.Q00119_LastImperialPrince.Q00119_LastImperialPrince;
import org.l2junity.scripts.quests.Q00124_MeetingTheElroki.Q00124_MeetingTheElroki;
import org.l2junity.scripts.quests.Q00125_TheNameOfEvil1.Q00125_TheNameOfEvil1;
import org.l2junity.scripts.quests.Q00126_TheNameOfEvil2.Q00126_TheNameOfEvil2;
import org.l2junity.scripts.quests.Q00128_PailakaSongOfIceAndFire.Q00128_PailakaSongOfIceAndFire;
import org.l2junity.scripts.quests.Q00129_PailakaDevilsLegacy.Q00129_PailakaDevilsLegacy;
import org.l2junity.scripts.quests.Q00134_TempleMissionary.Q00134_TempleMissionary;
import org.l2junity.scripts.quests.Q00135_TempleExecutor.Q00135_TempleExecutor;
import org.l2junity.scripts.quests.Q00136_MoreThanMeetsTheEye.Q00136_MoreThanMeetsTheEye;
import org.l2junity.scripts.quests.Q00137_TempleChampionPart1.Q00137_TempleChampionPart1;
import org.l2junity.scripts.quests.Q00138_TempleChampionPart2.Q00138_TempleChampionPart2;
import org.l2junity.scripts.quests.Q00139_ShadowFoxPart1.Q00139_ShadowFoxPart1;
import org.l2junity.scripts.quests.Q00140_ShadowFoxPart2.Q00140_ShadowFoxPart2;
import org.l2junity.scripts.quests.Q00141_ShadowFoxPart3.Q00141_ShadowFoxPart3;
import org.l2junity.scripts.quests.Q00142_FallenAngelRequestOfDawn.Q00142_FallenAngelRequestOfDawn;
import org.l2junity.scripts.quests.Q00143_FallenAngelRequestOfDusk.Q00143_FallenAngelRequestOfDusk;
import org.l2junity.scripts.quests.Q00144_PailakaInjuredDragon.Q00144_PailakaInjuredDragon;
import org.l2junity.scripts.quests.Q00146_TheZeroHour.Q00146_TheZeroHour;
import org.l2junity.scripts.quests.Q00149_PrimalMotherIstina.Q00149_PrimalMotherIstina;
import org.l2junity.scripts.quests.Q00177_SplitDestiny.Q00177_SplitDestiny;
import org.l2junity.scripts.quests.Q00183_RelicExploration.Q00183_RelicExploration;
import org.l2junity.scripts.quests.Q00184_ArtOfPersuasion.Q00184_ArtOfPersuasion;
import org.l2junity.scripts.quests.Q00185_NikolasCooperation.Q00185_NikolasCooperation;
import org.l2junity.scripts.quests.Q00186_ContractExecution.Q00186_ContractExecution;
import org.l2junity.scripts.quests.Q00187_NikolasHeart.Q00187_NikolasHeart;
import org.l2junity.scripts.quests.Q00188_SealRemoval.Q00188_SealRemoval;
import org.l2junity.scripts.quests.Q00189_ContractCompletion.Q00189_ContractCompletion;
import org.l2junity.scripts.quests.Q00190_LostDream.Q00190_LostDream;
import org.l2junity.scripts.quests.Q00191_VainConclusion.Q00191_VainConclusion;
import org.l2junity.scripts.quests.Q00192_SevenSignsSeriesOfDoubt.Q00192_SevenSignsSeriesOfDoubt;
import org.l2junity.scripts.quests.Q00193_SevenSignsDyingMessage.Q00193_SevenSignsDyingMessage;
import org.l2junity.scripts.quests.Q00194_SevenSignsMammonsContract.Q00194_SevenSignsMammonsContract;
import org.l2junity.scripts.quests.Q00195_SevenSignsSecretRitualOfThePriests.Q00195_SevenSignsSecretRitualOfThePriests;
import org.l2junity.scripts.quests.Q00196_SevenSignsSealOfTheEmperor.Q00196_SevenSignsSealOfTheEmperor;
import org.l2junity.scripts.quests.Q00197_SevenSignsTheSacredBookOfSeal.Q00197_SevenSignsTheSacredBookOfSeal;
import org.l2junity.scripts.quests.Q00198_SevenSignsEmbryo.Q00198_SevenSignsEmbryo;
import org.l2junity.scripts.quests.Q00210_ObtainAWolfPet.Q00210_ObtainAWolfPet;
import org.l2junity.scripts.quests.Q00237_WindsOfChange.Q00237_WindsOfChange;
import org.l2junity.scripts.quests.Q00238_SuccessFailureOfBusiness.Q00238_SuccessFailureOfBusiness;
import org.l2junity.scripts.quests.Q00239_WontYouJoinUs.Q00239_WontYouJoinUs;
import org.l2junity.scripts.quests.Q00240_ImTheOnlyOneYouCanTrust.Q00240_ImTheOnlyOneYouCanTrust;
import org.l2junity.scripts.quests.Q00254_LegendaryTales.Q00254_LegendaryTales;
import org.l2junity.scripts.quests.Q00270_TheOneWhoEndsSilence.Q00270_TheOneWhoEndsSilence;
import org.l2junity.scripts.quests.Q00278_HomeSecurity.Q00278_HomeSecurity;
import org.l2junity.scripts.quests.Q00279_TargetOfOpportunity.Q00279_TargetOfOpportunity;
import org.l2junity.scripts.quests.Q00300_HuntingLetoLizardman.Q00300_HuntingLetoLizardman;
import org.l2junity.scripts.quests.Q00310_OnlyWhatRemains.Q00310_OnlyWhatRemains;
import org.l2junity.scripts.quests.Q00371_ShrieksOfGhosts.Q00371_ShrieksOfGhosts;
import org.l2junity.scripts.quests.Q00420_LittleWing.Q00420_LittleWing;
import org.l2junity.scripts.quests.Q00421_LittleWingsBigAdventure.Q00421_LittleWingsBigAdventure;
import org.l2junity.scripts.quests.Q00450_GraveRobberRescue.Q00450_GraveRobberRescue;
import org.l2junity.scripts.quests.Q00451_LuciensAltar.Q00451_LuciensAltar;
import org.l2junity.scripts.quests.Q00452_FindingtheLostSoldiers.Q00452_FindingtheLostSoldiers;
import org.l2junity.scripts.quests.Q00453_NotStrongEnoughAlone.Q00453_NotStrongEnoughAlone;
import org.l2junity.scripts.quests.Q00455_WingsOfSand.Q00455_WingsOfSand;
import org.l2junity.scripts.quests.Q00456_DontKnowDontCare.Q00456_DontKnowDontCare;
import org.l2junity.scripts.quests.Q00457_LostAndFound.Q00457_LostAndFound;
import org.l2junity.scripts.quests.Q00458_PerfectForm.Q00458_PerfectForm;
import org.l2junity.scripts.quests.Q00464_Oath.Q00464_Oath;
import org.l2junity.scripts.quests.Q00470_DivinityProtector.Q00470_DivinityProtector;
import org.l2junity.scripts.quests.Q00474_WaitingForTheSummer.Q00474_WaitingForTheSummer;
import org.l2junity.scripts.quests.Q00476_PlainMission.Q00476_PlainMission;
import org.l2junity.scripts.quests.Q00485_HotSpringWater.Q00485_HotSpringWater;
import org.l2junity.scripts.quests.Q00488_WondersOfCaring.Q00488_WondersOfCaring;
import org.l2junity.scripts.quests.Q00489_InThisQuietPlace.Q00489_InThisQuietPlace;
import org.l2junity.scripts.quests.Q00490_DutyOfTheSurvivor.Q00490_DutyOfTheSurvivor;
import org.l2junity.scripts.quests.Q00492_TombRaiders.Q00492_TombRaiders;
import org.l2junity.scripts.quests.Q00493_KickingOutUnwelcomeGuests.Q00493_KickingOutUnwelcomeGuests;
import org.l2junity.scripts.quests.Q00501_ProofOfClanAlliance.Q00501_ProofOfClanAlliance;
import org.l2junity.scripts.quests.Q00508_AClansReputation.Q00508_AClansReputation;
import org.l2junity.scripts.quests.Q00509_AClansFame.Q00509_AClansFame;
import org.l2junity.scripts.quests.Q00510_AClansPrestige.Q00510_AClansPrestige;
import org.l2junity.scripts.quests.Q00511_AwlUnderFoot.Q00511_AwlUnderFoot;
import org.l2junity.scripts.quests.Q00551_OlympiadStarter.Q00551_OlympiadStarter;
import org.l2junity.scripts.quests.Q00553_OlympiadUndefeated.Q00553_OlympiadUndefeated;
import org.l2junity.scripts.quests.Q00617_GatherTheFlames.Q00617_GatherTheFlames;
import org.l2junity.scripts.quests.Q00618_IntoTheFlame.Q00618_IntoTheFlame;
import org.l2junity.scripts.quests.Q00621_EggDelivery.Q00621_EggDelivery;
import org.l2junity.scripts.quests.Q00622_SpecialtyLiquorDelivery.Q00622_SpecialtyLiquorDelivery;
import org.l2junity.scripts.quests.Q00623_TheFinestFood.Q00623_TheFinestFood;
import org.l2junity.scripts.quests.Q00627_HeartInSearchOfPower.Q00627_HeartInSearchOfPower;
import org.l2junity.scripts.quests.Q00631_DeliciousTopChoiceMeat.Q00631_DeliciousTopChoiceMeat;
import org.l2junity.scripts.quests.Q00641_AttackSailren.Q00641_AttackSailren;
import org.l2junity.scripts.quests.Q00642_APowerfulPrimevalCreature.Q00642_APowerfulPrimevalCreature;
import org.l2junity.scripts.quests.Q00643_RiseAndFallOfTheElrokiTribe.Q00643_RiseAndFallOfTheElrokiTribe;
import org.l2junity.scripts.quests.Q00645_GhostsOfBatur.Q00645_GhostsOfBatur;
import org.l2junity.scripts.quests.Q00648_AnIceMerchantsDream.Q00648_AnIceMerchantsDream;
import org.l2junity.scripts.quests.Q00662_AGameOfCards.Q00662_AGameOfCards;
import org.l2junity.scripts.quests.Q00688_DefeatTheElrokianRaiders.Q00688_DefeatTheElrokianRaiders;
import org.l2junity.scripts.quests.Q00760_BlockTheExit.Q00760_BlockTheExit;
import org.l2junity.scripts.quests.Q00761_AssistingTheGoldenRamArmy.Q00761_AssistingTheGoldenRamArmy;
import org.l2junity.scripts.quests.Q00762_AnOminousRequest.Q00762_AnOminousRequest;
import org.l2junity.scripts.quests.Q00763_ADauntingTask.Q00763_ADauntingTask;
import org.l2junity.scripts.quests.Q00901_HowLavasaurusesAreMade.Q00901_HowLavasaurusesAreMade;
import org.l2junity.scripts.quests.Q00902_ReclaimOurEra.Q00902_ReclaimOurEra;
import org.l2junity.scripts.quests.Q00903_TheCallOfAntharas.Q00903_TheCallOfAntharas;
import org.l2junity.scripts.quests.Q00904_DragonTrophyAntharas.Q00904_DragonTrophyAntharas;
import org.l2junity.scripts.quests.Q00905_RefinedDragonBlood.Q00905_RefinedDragonBlood;
import org.l2junity.scripts.quests.Q00906_TheCallOfValakas.Q00906_TheCallOfValakas;
import org.l2junity.scripts.quests.Q00907_DragonTrophyValakas.Q00907_DragonTrophyValakas;
import org.l2junity.scripts.quests.Q00998_FallenAngelSelect.Q00998_FallenAngelSelect;
import org.l2junity.scripts.quests.Q10273_GoodDayToFly.Q10273_GoodDayToFly;
import org.l2junity.scripts.quests.Q10274_CollectingInTheAir.Q10274_CollectingInTheAir;
import org.l2junity.scripts.quests.Q10275_ContainingTheAttributePower.Q10275_ContainingTheAttributePower;
import org.l2junity.scripts.quests.Q10282_ToTheSeedOfAnnihilation.Q10282_ToTheSeedOfAnnihilation;
import org.l2junity.scripts.quests.Q10283_RequestOfIceMerchant.Q10283_RequestOfIceMerchant;
import org.l2junity.scripts.quests.Q10284_AcquisitionOfDivineSword.Q10284_AcquisitionOfDivineSword;
import org.l2junity.scripts.quests.Q10285_MeetingSirra.Q10285_MeetingSirra;
import org.l2junity.scripts.quests.Q10286_ReunionWithSirra.Q10286_ReunionWithSirra;
import org.l2junity.scripts.quests.Q10287_StoryOfThoseLeft.Q10287_StoryOfThoseLeft;
import org.l2junity.scripts.quests.Q10288_SecretMission.Q10288_SecretMission;
import org.l2junity.scripts.quests.Q10289_FadeToBlack.Q10289_FadeToBlack;
import org.l2junity.scripts.quests.Q10290_LandDragonConqueror.Q10290_LandDragonConqueror;
import org.l2junity.scripts.quests.Q10291_FireDragonDestroyer.Q10291_FireDragonDestroyer;
import org.l2junity.scripts.quests.Q10292_SevenSignsGirlOfDoubt.Q10292_SevenSignsGirlOfDoubt;
import org.l2junity.scripts.quests.Q10293_SevenSignsForbiddenBookOfTheElmoreAdenKingdom.Q10293_SevenSignsForbiddenBookOfTheElmoreAdenKingdom;
import org.l2junity.scripts.quests.Q10294_SevenSignsToTheMonasteryOfSilence.Q10294_SevenSignsToTheMonasteryOfSilence;
import org.l2junity.scripts.quests.Q10297_GrandOpeningComeToOurPub.Q10297_GrandOpeningComeToOurPub;
import org.l2junity.scripts.quests.Q10301_ShadowOfTerrorBlackishRedFog.Q10301_ShadowOfTerrorBlackishRedFog;
import org.l2junity.scripts.quests.Q10302_UnsettlingShadowAndRumors.Q10302_UnsettlingShadowAndRumors;
import org.l2junity.scripts.quests.Q10305_UnstoppableFutileEfforts.Q10305_UnstoppableFutileEfforts;
import org.l2junity.scripts.quests.Q10306_TheCorruptedLeader.Q10306_TheCorruptedLeader;
import org.l2junity.scripts.quests.Q10320_LetsGoToTheCentralSquare.Q10320_LetsGoToTheCentralSquare;
import org.l2junity.scripts.quests.Q10321_QualificationsOfTheSeeker.Q10321_QualificationsOfTheSeeker;
import org.l2junity.scripts.quests.Q10330_ToTheRuinsOfYeSagira.Q10330_ToTheRuinsOfYeSagira;
import org.l2junity.scripts.quests.Q10331_StartOfFate.Q10331_StartOfFate;
import org.l2junity.scripts.quests.Q10332_ToughRoad.Q10332_ToughRoad;
import org.l2junity.scripts.quests.Q10333_DisappearedSakum.Q10333_DisappearedSakum;
import org.l2junity.scripts.quests.Q10334_ReportingTheStatusOfTheWindmillHill.Q10334_ReportingTheStatusOfTheWindmillHill;
import org.l2junity.scripts.quests.Q10335_RequestToFindSakum.Q10335_RequestToFindSakum;
import org.l2junity.scripts.quests.Q10336_DividedSakumKanilov.Q10336_DividedSakumKanilov;
import org.l2junity.scripts.quests.Q10337_SakumsImpact.Q10337_SakumsImpact;
import org.l2junity.scripts.quests.Q10338_SeizeYourDestiny.Q10338_SeizeYourDestiny;
import org.l2junity.scripts.quests.Q10339_FightingTheForgotten.Q10339_FightingTheForgotten;
import org.l2junity.scripts.quests.Q10341_DayOfDestinyHumansFate.Q10341_DayOfDestinyHumansFate;
import org.l2junity.scripts.quests.Q10342_DayOfDestinyElvenFate.Q10342_DayOfDestinyElvenFate;
import org.l2junity.scripts.quests.Q10343_DayOfDestinyDarkElfsFate.Q10343_DayOfDestinyDarkElfsFate;
import org.l2junity.scripts.quests.Q10344_DayOfDestinyOrcsFate.Q10344_DayOfDestinyOrcsFate;
import org.l2junity.scripts.quests.Q10345_DayOfDestinyDwarfsFate.Q10345_DayOfDestinyDwarfsFate;
import org.l2junity.scripts.quests.Q10346_DayOfDestinyKamaelsFate.Q10346_DayOfDestinyKamaelsFate;
import org.l2junity.scripts.quests.Q10358_DividedSakumPoslof.Q10358_DividedSakumPoslof;
import org.l2junity.scripts.quests.Q10359_TracesOfEvil.Q10359_TracesOfEvil;
import org.l2junity.scripts.quests.Q10360_CertificationOfFate.Q10360_CertificationOfFate;
import org.l2junity.scripts.quests.Q10362_CertificationOfTheSeeker.Q10362_CertificationOfTheSeeker;
import org.l2junity.scripts.quests.Q10363_RequestOfTheSeeker.Q10363_RequestOfTheSeeker;
import org.l2junity.scripts.quests.Q10364_ObligationsOfTheSeeker.Q10364_ObligationsOfTheSeeker;
import org.l2junity.scripts.quests.Q10365_ForTheSearchdogKing.Q10365_ForTheSearchdogKing;
import org.l2junity.scripts.quests.Q10366_ReportOnTheSituationAtTheRuins.Q10366_ReportOnTheSituationAtTheRuins;
import org.l2junity.scripts.quests.Q10369_NoblesseSoulTesting.Q10369_NoblesseSoulTesting;
import org.l2junity.scripts.quests.Q10370_MenacingTimes.Q10370_MenacingTimes;
import org.l2junity.scripts.quests.Q10371_GraspThyPower.Q10371_GraspThyPower;
import org.l2junity.scripts.quests.Q10372_PurgatoryVolvere.Q10372_PurgatoryVolvere;
import org.l2junity.scripts.quests.Q10374_ThatPlaceSuccubus.Q10374_ThatPlaceSuccubus;
import org.l2junity.scripts.quests.Q10375_SuccubusDisciples.Q10375_SuccubusDisciples;
import org.l2junity.scripts.quests.Q10377_TheInvadedExecutionGrounds.Q10377_TheInvadedExecutionGrounds;
import org.l2junity.scripts.quests.Q10378_WeedingWork.Q10378_WeedingWork;
import org.l2junity.scripts.quests.Q10381_ToTheSeedOfHellfire.Q10381_ToTheSeedOfHellfire;
import org.l2junity.scripts.quests.Q10385_RedThreadOfFate.Q10385_RedThreadOfFate;
import org.l2junity.scripts.quests.Q10386_MysteriousJourney.Q10386_MysteriousJourney;
import org.l2junity.scripts.quests.Q10387_SoullessOne.Q10387_SoullessOne;
import org.l2junity.scripts.quests.Q10388_ConspiracyBehindDoors.Q10388_ConspiracyBehindDoors;
import org.l2junity.scripts.quests.Q10389_TheVoiceOfAuthority.Q10389_TheVoiceOfAuthority;
import org.l2junity.scripts.quests.Q10390_KekropusLetter.Q10390_KekropusLetter;
import org.l2junity.scripts.quests.Q10391_ASuspiciousHelper.Q10391_ASuspiciousHelper;
import org.l2junity.scripts.quests.Q10392_FailureAndItsConsequences.Q10392_FailureAndItsConsequences;
import org.l2junity.scripts.quests.Q10393_KekropusLetterAClueCompleted.Q10393_KekropusLetterAClueCompleted;
import org.l2junity.scripts.quests.Q10394_MutualBenefit.Q10394_MutualBenefit;
import org.l2junity.scripts.quests.Q10395_NotATraitor.Q10395_NotATraitor;
import org.l2junity.scripts.quests.Q10397_KekropusLetterASuspiciousBadge.Q10397_KekropusLetterASuspiciousBadge;
import org.l2junity.scripts.quests.Q10398_ASuspiciousBadge.Q10398_ASuspiciousBadge;
import org.l2junity.scripts.quests.Q10399_TheAlphabetOfTheGiants.Q10399_TheAlphabetOfTheGiants;
import org.l2junity.scripts.quests.Q10401_KekropusLetterDecodingTheBadge.Q10401_KekropusLetterDecodingTheBadge;
import org.l2junity.scripts.quests.Q10402_NowhereToTurn.Q10402_NowhereToTurn;
import org.l2junity.scripts.quests.Q10403_TheGuardianGiant.Q10403_TheGuardianGiant;
import org.l2junity.scripts.quests.Q10404_KekropusLetterAHiddenMeaning.Q10404_KekropusLetterAHiddenMeaning;
import org.l2junity.scripts.quests.Q10405_KartiasSeed.Q10405_KartiasSeed;
import org.l2junity.scripts.quests.Q10406_BeforeDarknessBearsFruit.Q10406_BeforeDarknessBearsFruit;
import org.l2junity.scripts.quests.Q10408_KekropusLetterTheSwampOfScreams.Q10408_KekropusLetterTheSwampOfScreams;
import org.l2junity.scripts.quests.Q10409_ASuspiciousVagabondInTheSwamp.Q10409_ASuspiciousVagabondInTheSwamp;
import org.l2junity.scripts.quests.Q10410_EmbryoInTheSwampOfScreams.Q10410_EmbryoInTheSwampOfScreams;
import org.l2junity.scripts.quests.Q10411_KekropusLetterTheForestOfTheDead.Q10411_KekropusLetterTheForestOfTheDead;
import org.l2junity.scripts.quests.Q10412_ASuspiciousVagabondInTheForest.Q10412_ASuspiciousVagabondInTheForest;
import org.l2junity.scripts.quests.Q10413_EmbryoInTheForestOfTheDead.Q10413_EmbryoInTheForestOfTheDead;
import org.l2junity.scripts.quests.Q10414_KekropusLetterWithCourage.Q10414_KekropusLetterWithCourage;
import org.l2junity.scripts.quests.Q10416_InSearchOfTheEyeOfArgos.Q10416_InSearchOfTheEyeOfArgos;
import org.l2junity.scripts.quests.Q10417_DaimonTheWhiteEyed.Q10417_DaimonTheWhiteEyed;
import org.l2junity.scripts.quests.Q10421_AssassinationOfTheVarkaSilenosCommander.Q10421_AssassinationOfTheVarkaSilenosCommander;
import org.l2junity.scripts.quests.Q10442_TheAnnihilatedPlains1.Q10442_TheAnnihilatedPlains1;
import org.l2junity.scripts.quests.Q10445_AnImpendingThreat.Q10445_AnImpendingThreat;
import org.l2junity.scripts.quests.Q10450_ADarkAmbition.Q10450_ADarkAmbition;
import org.l2junity.scripts.quests.Q10460_ReturnOfTheAlligatorHunter.Q10460_ReturnOfTheAlligatorHunter;
import org.l2junity.scripts.quests.Q10472_WindsOfFateEncroachingShadows.Q10472_WindsOfFateEncroachingShadows;
import org.l2junity.scripts.quests.Q10501_ZakenEmbroideredSoulCloak.Q10501_ZakenEmbroideredSoulCloak;
import org.l2junity.scripts.quests.Q10502_FreyaEmbroideredSoulCloak.Q10502_FreyaEmbroideredSoulCloak;
import org.l2junity.scripts.quests.Q10503_FrintezzaEmbroideredSoulCloak.Q10503_FrintezzaEmbroideredSoulCloak;
import org.l2junity.scripts.quests.Q10504_JewelOfAntharas.Q10504_JewelOfAntharas;
import org.l2junity.scripts.quests.Q10505_JewelOfValakas.Q10505_JewelOfValakas;
import org.l2junity.scripts.quests.Q10541_TrainLikeItsRealThing.Q10541_TrainLikeItsRealThing;
import org.l2junity.scripts.quests.Q10542_SearchingForNewPower.Q10542_SearchingForNewPower;
import org.l2junity.scripts.quests.Q10543_SheddingWeight.Q10543_SheddingWeight;
import org.l2junity.scripts.quests.Q10544_CommandoSupplies.Q10544_CommandoSupplies;
import org.l2junity.scripts.quests.Q10701_TheRoadToDestruction.Q10701_TheRoadToDestruction;
import org.l2junity.scripts.quests.Q10702_TheRoadToInfinity.Q10702_TheRoadToInfinity;
import org.l2junity.scripts.quests.Q10707_FlamesOfSorrow.Q10707_FlamesOfSorrow;
import org.l2junity.scripts.quests.Q10708_StrengthenTheBarrier.Q10708_StrengthenTheBarrier;
import org.l2junity.scripts.quests.Q10709_TheStolenSeed.Q10709_TheStolenSeed;
import org.l2junity.scripts.quests.Q10710_LifeEnergyRepository.Q10710_LifeEnergyRepository;
import org.l2junity.scripts.quests.Q10732_AForeignLand.Q10732_AForeignLand;
import org.l2junity.scripts.quests.Q10733_TheTestForSurvival.Q10733_TheTestForSurvival;
import org.l2junity.scripts.quests.Q10734_DoOrDie.Q10734_DoOrDie;
import org.l2junity.scripts.quests.Q10735_ASpecialPower.Q10735_ASpecialPower;
import org.l2junity.scripts.quests.Q10736_ASpecialPower.Q10736_ASpecialPower;
import org.l2junity.scripts.quests.Q10737_GrakonsWarehouse.Q10737_GrakonsWarehouse;
import org.l2junity.scripts.quests.Q10738_AnInnerBeauty.Q10738_AnInnerBeauty;
import org.l2junity.scripts.quests.Q10739_SupplyAndDemand.Q10739_SupplyAndDemand;
import org.l2junity.scripts.quests.Q10740_NeverForget.Q10740_NeverForget;
import org.l2junity.scripts.quests.Q10741_ADraughtForTheCold.Q10741_ADraughtForTheCold;
import org.l2junity.scripts.quests.Q10742_AFurryFriend.Q10742_AFurryFriend;
import org.l2junity.scripts.quests.Q10743_StrangeFungus.Q10743_StrangeFungus;
import org.l2junity.scripts.quests.Q10744_StrongerThanSteel.Q10744_StrongerThanSteel;
import org.l2junity.scripts.quests.Q10745_TheSecretIngredients.Q10745_TheSecretIngredients;
import org.l2junity.scripts.quests.Q10746_SeeTheWorld.Q10746_SeeTheWorld;
import org.l2junity.scripts.quests.Q10751_WindsOfFateEncounters.Q10751_WindsOfFateEncounters;
import org.l2junity.scripts.quests.Q10752_WindsOfFateAPromise.Q10752_WindsOfFateAPromise;
import org.l2junity.scripts.quests.Q10755_LettersFromTheQueenWindyHill.Q10755_LettersFromTheQueenWindyHill;
import org.l2junity.scripts.quests.Q10756_AnInterdimensionalDraft.Q10756_AnInterdimensionalDraft;
import org.l2junity.scripts.quests.Q10757_QuietingTheStorm.Q10757_QuietingTheStorm;
import org.l2junity.scripts.quests.Q10758_TheOathOfTheWind.Q10758_TheOathOfTheWind;
import org.l2junity.scripts.quests.Q10760_LettersFromTheQueenOrcBarracks.Q10760_LettersFromTheQueenOrcBarracks;
import org.l2junity.scripts.quests.Q10761_AnOrcInLove.Q10761_AnOrcInLove;
import org.l2junity.scripts.quests.Q10762_MarionetteSpirit.Q10762_MarionetteSpirit;
import org.l2junity.scripts.quests.Q10763_TerrifyingChertuba.Q10763_TerrifyingChertuba;
import org.l2junity.scripts.quests.Q10764_FreeSpirit.Q10764_FreeSpirit;
import org.l2junity.scripts.quests.Q10769_LettersFromTheQueenCrumaTowerPart1.Q10769_LettersFromTheQueenCrumaTowerPart1;
import org.l2junity.scripts.quests.Q10770_InSearchOfTheGrail.Q10770_InSearchOfTheGrail;
import org.l2junity.scripts.quests.Q10771_VolatilePower.Q10771_VolatilePower;
import org.l2junity.scripts.quests.Q10772_ReportsFromCrumaTowerPart1.Q10772_ReportsFromCrumaTowerPart1;
import org.l2junity.scripts.quests.Q10774_LettersFromTheQueenCrumaTowerPart2.Q10774_LettersFromTheQueenCrumaTowerPart2;
import org.l2junity.scripts.quests.Q10775_InSearchOfAnAncientGiant.Q10775_InSearchOfAnAncientGiant;
import org.l2junity.scripts.quests.Q10776_TheWrathOfTheGiants.Q10776_TheWrathOfTheGiants;
import org.l2junity.scripts.quests.Q10777_ReportsFromCrumaTowerPart2.Q10777_ReportsFromCrumaTowerPart2;
import org.l2junity.scripts.quests.Q10779_LettersFromTheQueenSeaOfSpores.Q10779_LettersFromTheQueenSeaOfSpores;
import org.l2junity.scripts.quests.Q10780_AWeakenedBarrier.Q10780_AWeakenedBarrier;
import org.l2junity.scripts.quests.Q10781_IngredientsToEnforcements.Q10781_IngredientsToEnforcements;
import org.l2junity.scripts.quests.Q10782_LettersFromTheQueenForsakenPlains.Q10782_LettersFromTheQueenForsakenPlains;
import org.l2junity.scripts.quests.Q10783_TracesOfAnAmbush.Q10783_TracesOfAnAmbush;
import org.l2junity.scripts.quests.Q10784_TheBrokenDevice.Q10784_TheBrokenDevice;
import org.l2junity.scripts.quests.Q10785_LettersFromTheQueenFieldsOfMassacre.Q10785_LettersFromTheQueenFieldsOfMassacre;
import org.l2junity.scripts.quests.Q10786_ResidentProblemSolver.Q10786_ResidentProblemSolver;
import org.l2junity.scripts.quests.Q10787_ASpyMission.Q10787_ASpyMission;
import org.l2junity.scripts.quests.Q10789_LettersFromTheQueenSwampOfScreams.Q10789_LettersFromTheQueenSwampOfScreams;
import org.l2junity.scripts.quests.Q10792_LettersFromTheQueenForestOfTheDead.Q10792_LettersFromTheQueenForestOfTheDead;
import org.l2junity.scripts.quests.Q10793_SaveTheSouls.Q10793_SaveTheSouls;
import org.l2junity.scripts.quests.Q10811_ExaltedOneWhoFacesTheLimit.Q10811_ExaltedOneWhoFacesTheLimit;
import org.l2junity.scripts.quests.Q10812_FacingSadness.Q10812_FacingSadness;
import org.l2junity.scripts.quests.Q10813_ForGlory.Q10813_ForGlory;
import org.l2junity.scripts.quests.Q10814_BefittingOfTheStatus.Q10814_BefittingOfTheStatus;
import org.l2junity.scripts.quests.Q10815_StepUp.Q10815_StepUp;

/**
 * @author NosBit
 */
public class QuestMasterHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(QuestMasterHandler.class);
	
	private static final Class<?>[] QUESTS =
	{
		Q00013_ParcelDelivery.class,
		Q00015_SweetWhispers.class,
		Q00016_TheComingDarkness.class,
		Q00017_LightAndDarkness.class,
		Q00019_GoToThePastureland.class,
		Q00020_BringUpWithLove.class,
		Q00031_SecretBuriedInTheSwamp.class,
		Q00032_AnObviousLie.class,
		Q00033_MakeAPairOfDressShoes.class,
		Q00034_InSearchOfCloth.class,
		Q00035_FindGlitteringJewelry.class,
		Q00036_MakeASewingKit.class,
		Q00037_MakeFormalWear.class,
		Q00040_ASpecialOrder.class,
		Q00042_HelpTheUncle.class,
		Q00043_HelpTheSister.class,
		Q00044_HelpTheSon.class,
		Q00061_LawEnforcement.class,
		Q00109_InSearchOfTheNest.class,
		Q00110_ToThePrimevalIsle.class,
		Q00111_ElrokianHuntersProof.class,
		Q00115_TheOtherSideOfTruth.class,
		Q00119_LastImperialPrince.class,
		Q00124_MeetingTheElroki.class,
		Q00125_TheNameOfEvil1.class,
		Q00126_TheNameOfEvil2.class,
		Q00128_PailakaSongOfIceAndFire.class,
		Q00129_PailakaDevilsLegacy.class,
		Q00134_TempleMissionary.class,
		Q00135_TempleExecutor.class,
		Q00136_MoreThanMeetsTheEye.class,
		Q00137_TempleChampionPart1.class,
		Q00138_TempleChampionPart2.class,
		Q00139_ShadowFoxPart1.class,
		Q00140_ShadowFoxPart2.class,
		Q00141_ShadowFoxPart3.class,
		Q00142_FallenAngelRequestOfDawn.class,
		Q00143_FallenAngelRequestOfDusk.class,
		Q00144_PailakaInjuredDragon.class,
		Q00146_TheZeroHour.class,
		Q00149_PrimalMotherIstina.class,
		Q00177_SplitDestiny.class,
		Q00183_RelicExploration.class,
		Q00184_ArtOfPersuasion.class,
		Q00185_NikolasCooperation.class,
		Q00186_ContractExecution.class,
		Q00187_NikolasHeart.class,
		Q00188_SealRemoval.class,
		Q00189_ContractCompletion.class,
		Q00190_LostDream.class,
		Q00191_VainConclusion.class,
		Q00192_SevenSignsSeriesOfDoubt.class,
		Q00193_SevenSignsDyingMessage.class,
		Q00194_SevenSignsMammonsContract.class,
		Q00195_SevenSignsSecretRitualOfThePriests.class,
		Q00196_SevenSignsSealOfTheEmperor.class,
		Q00197_SevenSignsTheSacredBookOfSeal.class,
		Q00198_SevenSignsEmbryo.class,
		Q00210_ObtainAWolfPet.class,
		Q00237_WindsOfChange.class,
		Q00238_SuccessFailureOfBusiness.class,
		Q00239_WontYouJoinUs.class,
		Q00240_ImTheOnlyOneYouCanTrust.class,
		Q00254_LegendaryTales.class,
		Q00270_TheOneWhoEndsSilence.class,
		Q00278_HomeSecurity.class,
		Q00279_TargetOfOpportunity.class,
		Q00300_HuntingLetoLizardman.class,
		Q00310_OnlyWhatRemains.class,
		Q00371_ShrieksOfGhosts.class,
		Q00420_LittleWing.class,
		Q00421_LittleWingsBigAdventure.class,
		Q00450_GraveRobberRescue.class,
		Q00451_LuciensAltar.class,
		Q00452_FindingtheLostSoldiers.class,
		Q00453_NotStrongEnoughAlone.class,
		Q00455_WingsOfSand.class,
		Q00456_DontKnowDontCare.class,
		Q00457_LostAndFound.class,
		Q00458_PerfectForm.class,
		Q00464_Oath.class,
		Q00470_DivinityProtector.class,
		Q00474_WaitingForTheSummer.class,
		Q00476_PlainMission.class,
		Q00485_HotSpringWater.class,
		Q00488_WondersOfCaring.class,
		Q00489_InThisQuietPlace.class,
		Q00490_DutyOfTheSurvivor.class,
		Q00492_TombRaiders.class,
		Q00493_KickingOutUnwelcomeGuests.class,
		Q00501_ProofOfClanAlliance.class,
		Q00508_AClansReputation.class,
		Q00509_AClansFame.class,
		Q00510_AClansPrestige.class,
		Q00511_AwlUnderFoot.class,
		Q00551_OlympiadStarter.class,
		Q00553_OlympiadUndefeated.class,
		Q00617_GatherTheFlames.class,
		Q00618_IntoTheFlame.class,
		Q00621_EggDelivery.class,
		Q00622_SpecialtyLiquorDelivery.class,
		Q00623_TheFinestFood.class,
		Q00627_HeartInSearchOfPower.class,
		Q00631_DeliciousTopChoiceMeat.class,
		Q00641_AttackSailren.class,
		Q00642_APowerfulPrimevalCreature.class,
		Q00643_RiseAndFallOfTheElrokiTribe.class,
		Q00645_GhostsOfBatur.class,
		Q00648_AnIceMerchantsDream.class,
		Q00662_AGameOfCards.class,
		Q00688_DefeatTheElrokianRaiders.class,
		Q00760_BlockTheExit.class,
		Q00761_AssistingTheGoldenRamArmy.class,
		Q00762_AnOminousRequest.class,
		Q00763_ADauntingTask.class,
		Q00901_HowLavasaurusesAreMade.class,
		Q00902_ReclaimOurEra.class,
		Q00903_TheCallOfAntharas.class,
		Q00904_DragonTrophyAntharas.class,
		Q00905_RefinedDragonBlood.class,
		Q00906_TheCallOfValakas.class,
		Q00907_DragonTrophyValakas.class,
		Q00998_FallenAngelSelect.class,
		Q10273_GoodDayToFly.class,
		Q10274_CollectingInTheAir.class,
		Q10275_ContainingTheAttributePower.class,
		Q10282_ToTheSeedOfAnnihilation.class,
		Q10283_RequestOfIceMerchant.class,
		Q10284_AcquisitionOfDivineSword.class,
		Q10285_MeetingSirra.class,
		Q10286_ReunionWithSirra.class,
		Q10287_StoryOfThoseLeft.class,
		Q10288_SecretMission.class,
		Q10289_FadeToBlack.class,
		Q10290_LandDragonConqueror.class,
		Q10291_FireDragonDestroyer.class,
		Q10292_SevenSignsGirlOfDoubt.class,
		Q10293_SevenSignsForbiddenBookOfTheElmoreAdenKingdom.class,
		Q10294_SevenSignsToTheMonasteryOfSilence.class,
		Q10297_GrandOpeningComeToOurPub.class,
		Q10301_ShadowOfTerrorBlackishRedFog.class,
		Q10302_UnsettlingShadowAndRumors.class,
		Q10305_UnstoppableFutileEfforts.class,
		Q10306_TheCorruptedLeader.class,
		Q10320_LetsGoToTheCentralSquare.class,
		Q10321_QualificationsOfTheSeeker.class,
		Q10330_ToTheRuinsOfYeSagira.class,
		Q10331_StartOfFate.class,
		Q10332_ToughRoad.class,
		Q10333_DisappearedSakum.class,
		Q10334_ReportingTheStatusOfTheWindmillHill.class,
		Q10335_RequestToFindSakum.class,
		Q10336_DividedSakumKanilov.class,
		Q10337_SakumsImpact.class,
		Q10338_SeizeYourDestiny.class,
		Q10339_FightingTheForgotten.class,
		Q10341_DayOfDestinyHumansFate.class,
		Q10342_DayOfDestinyElvenFate.class,
		Q10343_DayOfDestinyDarkElfsFate.class,
		Q10344_DayOfDestinyOrcsFate.class,
		Q10345_DayOfDestinyDwarfsFate.class,
		Q10346_DayOfDestinyKamaelsFate.class,
		Q10358_DividedSakumPoslof.class,
		Q10359_TracesOfEvil.class,
		Q10360_CertificationOfFate.class,
		Q10362_CertificationOfTheSeeker.class,
		Q10363_RequestOfTheSeeker.class,
		Q10364_ObligationsOfTheSeeker.class,
		Q10365_ForTheSearchdogKing.class,
		Q10366_ReportOnTheSituationAtTheRuins.class,
		Q10369_NoblesseSoulTesting.class,
		Q10370_MenacingTimes.class,
		Q10371_GraspThyPower.class,
		Q10372_PurgatoryVolvere.class,
		Q10374_ThatPlaceSuccubus.class,
		Q10375_SuccubusDisciples.class,
		Q10377_TheInvadedExecutionGrounds.class,
		Q10378_WeedingWork.class,
		Q10381_ToTheSeedOfHellfire.class,
		Q10385_RedThreadOfFate.class,
		Q10386_MysteriousJourney.class,
		Q10387_SoullessOne.class,
		Q10388_ConspiracyBehindDoors.class,
		Q10389_TheVoiceOfAuthority.class,
		Q10390_KekropusLetter.class,
		Q10391_ASuspiciousHelper.class,
		Q10392_FailureAndItsConsequences.class,
		Q10393_KekropusLetterAClueCompleted.class,
		Q10394_MutualBenefit.class,
		Q10395_NotATraitor.class,
		Q10397_KekropusLetterASuspiciousBadge.class,
		Q10398_ASuspiciousBadge.class,
		Q10399_TheAlphabetOfTheGiants.class,
		Q10401_KekropusLetterDecodingTheBadge.class,
		Q10402_NowhereToTurn.class,
		Q10403_TheGuardianGiant.class,
		Q10404_KekropusLetterAHiddenMeaning.class,
		Q10405_KartiasSeed.class,
		Q10406_BeforeDarknessBearsFruit.class,
		Q10408_KekropusLetterTheSwampOfScreams.class,
		Q10409_ASuspiciousVagabondInTheSwamp.class,
		Q10410_EmbryoInTheSwampOfScreams.class,
		Q10411_KekropusLetterTheForestOfTheDead.class,
		Q10412_ASuspiciousVagabondInTheForest.class,
		Q10413_EmbryoInTheForestOfTheDead.class,
		Q10414_KekropusLetterWithCourage.class,
		Q10416_InSearchOfTheEyeOfArgos.class,
		Q10417_DaimonTheWhiteEyed.class,
		Q10421_AssassinationOfTheVarkaSilenosCommander.class,
		Q10442_TheAnnihilatedPlains1.class,
		Q10445_AnImpendingThreat.class,
		Q10450_ADarkAmbition.class,
		Q10460_ReturnOfTheAlligatorHunter.class,
		Q10472_WindsOfFateEncroachingShadows.class,
		Q10501_ZakenEmbroideredSoulCloak.class,
		Q10502_FreyaEmbroideredSoulCloak.class,
		Q10503_FrintezzaEmbroideredSoulCloak.class,
		Q10504_JewelOfAntharas.class,
		Q10505_JewelOfValakas.class,
		Q10541_TrainLikeItsRealThing.class,
		Q10542_SearchingForNewPower.class,
		Q10543_SheddingWeight.class,
		Q10544_CommandoSupplies.class,
		Q10701_TheRoadToDestruction.class,
		Q10702_TheRoadToInfinity.class,
		Q10707_FlamesOfSorrow.class,
		Q10708_StrengthenTheBarrier.class,
		Q10709_TheStolenSeed.class,
		Q10710_LifeEnergyRepository.class,
		Q10732_AForeignLand.class,
		Q10733_TheTestForSurvival.class,
		Q10734_DoOrDie.class,
		Q10735_ASpecialPower.class,
		Q10736_ASpecialPower.class,
		Q10737_GrakonsWarehouse.class,
		Q10738_AnInnerBeauty.class,
		Q10739_SupplyAndDemand.class,
		Q10740_NeverForget.class,
		Q10741_ADraughtForTheCold.class,
		Q10742_AFurryFriend.class,
		Q10743_StrangeFungus.class,
		Q10744_StrongerThanSteel.class,
		Q10745_TheSecretIngredients.class,
		Q10746_SeeTheWorld.class,
		Q10751_WindsOfFateEncounters.class,
		Q10752_WindsOfFateAPromise.class,
		Q10755_LettersFromTheQueenWindyHill.class,
		Q10756_AnInterdimensionalDraft.class,
		Q10757_QuietingTheStorm.class,
		Q10758_TheOathOfTheWind.class,
		Q10760_LettersFromTheQueenOrcBarracks.class,
		Q10761_AnOrcInLove.class,
		Q10762_MarionetteSpirit.class,
		Q10763_TerrifyingChertuba.class,
		Q10764_FreeSpirit.class,
		Q10769_LettersFromTheQueenCrumaTowerPart1.class,
		Q10770_InSearchOfTheGrail.class,
		Q10771_VolatilePower.class,
		Q10772_ReportsFromCrumaTowerPart1.class,
		Q10774_LettersFromTheQueenCrumaTowerPart2.class,
		Q10775_InSearchOfAnAncientGiant.class,
		Q10776_TheWrathOfTheGiants.class,
		Q10777_ReportsFromCrumaTowerPart2.class,
		Q10779_LettersFromTheQueenSeaOfSpores.class,
		Q10780_AWeakenedBarrier.class,
		Q10781_IngredientsToEnforcements.class,
		Q10782_LettersFromTheQueenForsakenPlains.class,
		Q10783_TracesOfAnAmbush.class,
		Q10784_TheBrokenDevice.class,
		Q10785_LettersFromTheQueenFieldsOfMassacre.class,
		Q10786_ResidentProblemSolver.class,
		Q10787_ASpyMission.class,
		Q10789_LettersFromTheQueenSwampOfScreams.class,
		Q10792_LettersFromTheQueenForestOfTheDead.class,
		Q10793_SaveTheSouls.class,
		Q10811_ExaltedOneWhoFacesTheLimit.class,
		Q10812_FacingSadness.class,
		Q10813_ForGlory.class,
		Q10814_BefittingOfTheStatus.class,
		Q10815_StepUp.class
	};
	
	@GameScript
	public static void main()
	{
		for (Class<?> quest : QUESTS)
		{
			try
			{
				quest.newInstance();
			}
			catch (Exception e)
			{
				LOGGER.error(QuestMasterHandler.class.getSimpleName() + ": Failed loading " + quest.getSimpleName() + ":", e);
			}
		}
	}
}
