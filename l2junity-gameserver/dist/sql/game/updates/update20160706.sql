-- Teleport update
DROP TABLE IF EXISTS teleport;
-- Cloud Mountain fortress drop
DELETE FROM fort_spawnlist WHERE fortId = 113;
DELETE FROM fort_siege_guards WHERE fortId = 113;
DELETE FROM fort WHERE id = 113;