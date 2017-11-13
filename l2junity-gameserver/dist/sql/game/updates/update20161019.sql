INSERT INTO `item_variations` (`itemId`, `option1`, `option2`) (SELECT `itemId`, `augAttributes` >> 16, `augAttributes` & 0xffff FROM `item_attributes`);

DROP TABLE `item_attributes`;