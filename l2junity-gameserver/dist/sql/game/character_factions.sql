CREATE TABLE IF NOT EXISTS `character_factions` ( 
 `charId` INT(10) UNSIGNED NOT NULL DEFAULT '0',
 `faction_name` VARCHAR(40) NOT NULL DEFAULT '',
 `points` INT NOT NULL DEFAULT '0',
  PRIMARY KEY (`charId`, `faction_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;