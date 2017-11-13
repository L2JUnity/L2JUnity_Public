-- Drop character quests column
ALTER TABLE `character_quests` DROP COLUMN `class_index`;

-- Merge cond and __compltdStateFlags variables together
UPDATE
  character_quests dest,
  (
    SELECT
      name,
      MAX(CASE WHEN var LIKE "cond" THEN value ELSE null END) AS cond,
      MAX(CASE WHEN var LIKE "__compltdStateFlags" THEN value ELSE null END) AS stateFlags
    FROM character_quests
    WHERE var LIKE "cond" OR var LIKE "__compltdStateFlags"
    GROUP BY name
  ) source
SET dest.value =
  CASE
    WHEN source.stateFlags IS NOT NULL THEN source.stateFlags & 0x7FFFFFFF
    WHEN source.cond IS NOT NULL THEN (1 << source.cond) -1
  END
WHERE
  dest.name LIKE source.name AND dest.var LIKE "cond";

-- Delete __compltdStateFlags variables
DELETE FROM character_quests WHERE var LIKE "__compltdStateFlags";

-- Drop quest_global_data table
DROP TABLE quest_global_data;