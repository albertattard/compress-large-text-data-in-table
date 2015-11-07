--
-- #%L
-- Compress Large Text Data in Table
-- %%
-- Copyright (C) 2012 - 2015 Java Creed
-- %%
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--      http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- #L%
--
-- Drops the test tables if these exists and then recreates them
DROP TABLE IF EXISTS `large_text_table`;
DROP TABLE IF EXISTS `compressed_table`;

CREATE TABLE `large_text_table` (
  `id` bigint(18) unsigned NOT NULL AUTO_INCREMENT,
  `text` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


CREATE TABLE `compressed_table` (
  `id` bigint(18) unsigned NOT NULL AUTO_INCREMENT,
  `compressed` MEDIUMBLOB NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
