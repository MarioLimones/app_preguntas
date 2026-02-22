SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS quizapp CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE quizapp;
CREATE TABLE IF NOT EXISTS vf_questions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  statement TEXT,
  explanation TEXT,
  category_id VARCHAR(255),
  correct_answer TINYINT(1) NOT NULL
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS sc_questions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  statement TEXT,
  explanation TEXT,
  category_id VARCHAR(255),
  correct_index INT
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS sc_question_options (
  question_id BIGINT NOT NULL,
  option_index INT NOT NULL,
  option_text TEXT,
  PRIMARY KEY (question_id, option_index)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS mc_questions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  statement TEXT,
  explanation TEXT,
  category_id VARCHAR(255)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS mc_question_options (
  question_id BIGINT NOT NULL,
  option_index INT NOT NULL,
  option_text TEXT,
  PRIMARY KEY (question_id, option_index)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS mc_question_correct_indexes (
  question_id BIGINT NOT NULL,
  correct_index INT NOT NULL,
  PRIMARY KEY (question_id, correct_index)
) ENGINE=InnoDB;
INSERT INTO vf_questions (id, statement, explanation, category_id, correct_answer) VALUES (1, 'El Mundial de 2010 se jugÃ³ en SudÃ¡frica y lo ganÃ³ EspaÃ±a.', 'EspaÃ±a venciÃ³ a Holanda en la final con un gol de AndrÃ©s Iniesta en tiempo extra.', NULL, 1);
INSERT INTO vf_questions (id, statement, explanation, category_id, correct_answer) VALUES (2, 'La Juventus es el equipo con mÃ¡s Ligas de Campeones de la UEFA ganadas.', 'El equipo con mÃ¡s tÃ­tulos de Champions League es el Real Madrid.', NULL, 0);
INSERT INTO vf_questions (id, statement, explanation, category_id, correct_answer) VALUES (3, 'Diego Maradona anotÃ³ el famoso gol de ''La Mano de Dios'' contra Inglaterra.', 'Fue en los cuartos de final del Mundial de MÃ©xico 1986.', NULL, 1);
INSERT INTO vf_questions (id, statement, explanation, category_id, correct_answer) VALUES (4, 'Un partido de fÃºtbol estÃ¡ndar dura 120 minutos sin contar el alargue.', 'El tiempo reglamentario es de 90 minutos, dividido en dos tiempos de 45.', NULL, 0);
INSERT INTO vf_questions (id, statement, explanation, category_id, correct_answer) VALUES (5, 'Lionel Messi ha ganado mÃ¡s Balones de Oro que Cristiano Ronaldo.', 'Messi tiene el rÃ©cord absoluto de ganadores del BalÃ³n de Oro.', NULL, 1);
INSERT INTO vf_questions (id, statement, explanation, category_id, correct_answer) VALUES (6, 'Se marca fuera de juego (offside) en un saque de banda.', 'Las reglas establecen que no hay posiciÃ³n de fuera de juego directo desde un saque de banda.', NULL, 0);
INSERT INTO vf_questions (id, statement, explanation, category_id, correct_answer) VALUES (7, 'El Estadio Camp Nou es la casa del equipo FC Barcelona.', 'Es el estadio del FC Barcelona y uno de los mÃ¡s grandes del mundo.', NULL, 1);
INSERT INTO vf_questions (id, statement, explanation, category_id, correct_answer) VALUES (8, 'PelÃ© ganÃ³ tres Copas del Mundo con la selecciÃ³n nacional de Brasil.', 'LogrÃ³ levantar la copa en los torneos de 1958, 1962 y 1970.', NULL, 1);
INSERT INTO vf_questions (id, statement, explanation, category_id, correct_answer) VALUES (9, 'El Liverpool Football Club estÃ¡ ubicado en la ciudad de Londres.', 'El club estÃ¡ radicado en la ciudad de Liverpool, no en Londres.', NULL, 0);
INSERT INTO vf_questions (id, statement, explanation, category_id, correct_answer) VALUES (10, 'La tarjeta roja resulta en la expulsiÃ³n inmediata de un jugador del partido.', 'Un jugador amonestado con tarjeta roja debe abandonar el campo sin ser sustituido.', NULL, 1);
INSERT INTO sc_questions (id, statement, explanation, category_id, correct_index) VALUES (1, 'Â¿QuÃ© selecciÃ³n nacional ha ganado mÃ¡s Copas del Mundo de la FIFA?', 'Brasil lidera el palmarÃ©s histÃ³rico mundial con 5 tÃ­tulos.', NULL, 0);
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (1, 0, 'Brasil');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (1, 1, 'Alemania');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (1, 2, 'Italia');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (1, 3, 'Argentina');
INSERT INTO sc_questions (id, statement, explanation, category_id, correct_index) VALUES (2, 'Â¿QuÃ© equipo ganÃ³ la UEFA Champions League en la temporada 2022-2023?', 'Manchester City ganÃ³ la final ante el Inter de MilÃ¡n.', NULL, 1);
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (2, 0, 'Real Madrid');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (2, 1, 'Manchester City');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (2, 2, 'Bayern MÃºnich');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (2, 3, 'Liverpool');
INSERT INTO sc_questions (id, statement, explanation, category_id, correct_index) VALUES (3, 'Â¿QuiÃ©n es el mÃ¡ximo goleador histÃ³rico de la Liga de Campeones de la UEFA?', 'Cristiano Ronaldo es el lÃ­der de goleo en esta competiciÃ³n europea.', NULL, 2);
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (3, 0, 'Lionel Messi');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (3, 1, 'Robert Lewandowski');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (3, 2, 'Cristiano Ronaldo');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (3, 3, 'RaÃºl GonzÃ¡lez');
INSERT INTO sc_questions (id, statement, explanation, category_id, correct_index) VALUES (4, 'Â¿En quÃ© paÃ­s de Europa compite el Bayern MÃºnich?', 'Es una de las fuerzas dominantes de la Bundesliga alemana.', NULL, 2);
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (4, 0, 'Inglaterra');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (4, 1, 'EspaÃ±a');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (4, 2, 'Alemania');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (4, 3, 'Francia');
INSERT INTO sc_questions (id, statement, explanation, category_id, correct_index) VALUES (5, 'Â¿CuÃ¡l es el nombre del estadio donde juega el Real Madrid como local?', 'Es el imponente Estadio Santiago BernabÃ©u en Madrid.', NULL, 1);
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (5, 0, 'Camp Nou');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (5, 1, 'Santiago BernabÃ©u');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (5, 2, 'San Siro');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (5, 3, 'Old Trafford');
INSERT INTO sc_questions (id, statement, explanation, category_id, correct_index) VALUES (6, 'Â¿QuÃ© paÃ­s ganÃ³ la Copa del Mundo de la FIFA celebrada en 2022?', 'Argentina venciÃ³ a Francia en una emocionante definiciÃ³n por penales.', NULL, 2);
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (6, 0, 'Francia');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (6, 1, 'Croacia');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (6, 2, 'Argentina');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (6, 3, 'Brasil');
INSERT INTO sc_questions (id, statement, explanation, category_id, correct_index) VALUES (7, 'Â¿CuÃ¡l es la duraciÃ³n reglamentaria de un partido de fÃºtbol (sin alargue)?', 'El juego se divide en dos mitades de 45 minutos.', NULL, 1);
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (7, 0, '80 minutos');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (7, 1, '90 minutos');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (7, 2, '100 minutos');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (7, 3, '120 minutos');
INSERT INTO sc_questions (id, statement, explanation, category_id, correct_index) VALUES (8, 'Â¿QuÃ© equipo del fÃºtbol inglÃ©s es mundialmente conocido como ''Los Diablos Rojos''?', 'El histÃ³rico club Manchester United es apodado The Red Devils.', NULL, 3);
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (8, 0, 'Arsenal');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (8, 1, 'Chelsea');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (8, 2, 'Liverpool');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (8, 3, 'Manchester United');
INSERT INTO sc_questions (id, statement, explanation, category_id, correct_index) VALUES (9, 'Â¿QuiÃ©n ganÃ³ el BalÃ³n de Oro en el aÃ±o 2007 (antes de la hegemonÃ­a Messi-CR7)?', 'KakÃ¡ lo ganÃ³ por sus increÃ­bles temporadas con el AC Milan.', NULL, 0);
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (9, 0, 'KakÃ¡');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (9, 1, 'Ronaldinho');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (9, 2, 'Fabio Cannavaro');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (9, 3, 'Zinedine Zidane');
INSERT INTO sc_questions (id, statement, explanation, category_id, correct_index) VALUES (10, 'Â¿En quÃ© ciudad sudamericana se encuentra el mÃ­tico estadio ''La Bombonera''?', 'Es la casa del Club AtlÃ©tico Boca Juniors en Buenos Aires, Argentina.', NULL, 0);
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (10, 0, 'Buenos Aires');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (10, 1, 'SÃ£o Paulo');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (10, 2, 'Montevideo');
INSERT INTO sc_question_options (question_id, option_index, option_text) VALUES (10, 3, 'RÃ­o de Janeiro');
INSERT INTO mc_questions (id, statement, explanation, category_id) VALUES (1, 'Â¿CuÃ¡les de los siguientes equipos han ganado la Copa Libertadores de AmÃ©rica?', 'Boca, River y Flamengo son mÃºltiples campeones del histÃ³rico torneo sudamericano.', NULL);
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (1, 0, 'Boca Juniors');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (1, 1, 'River Plate');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (1, 2, 'Cruz Azul');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (1, 3, 'Flamengo');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (1, 4, 'LA Galaxy');
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (1, 0);
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (1, 1);
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (1, 3);
INSERT INTO mc_questions (id, statement, explanation, category_id) VALUES (2, 'Â¿QuÃ© paÃ­ses han sido anfitriones de una Copa Mundial de la FIFA en al menos dos ocasiones (incluyendo sedes compartidas futuras)?', 'MÃ©xico, Italia y Francia han repetido sede (y MÃ©xico va a tener su tercer mundial pronto).', NULL);
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (2, 0, 'MÃ©xico');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (2, 1, 'EspaÃ±a');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (2, 2, 'Italia');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (2, 3, 'Francia');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (2, 4, 'Argentina');
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (2, 0);
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (2, 2);
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (2, 3);
INSERT INTO mc_questions (id, statement, explanation, category_id) VALUES (3, 'Â¿QuÃ© jugadores conformaron el famoso tridente de ataque ''MSN'' del Barcelona?', 'Messi, SuÃ¡rez y Neymar formaron una de las delanteras mÃ¡s letales.', NULL);
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (3, 0, 'Lionel Messi');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (3, 1, 'Luis SuÃ¡rez');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (3, 2, 'Alexis SÃ¡nchez');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (3, 3, 'Neymar Jr');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (3, 4, 'Pedro RodrÃ­guez');
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (3, 0);
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (3, 1);
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (3, 3);
INSERT INTO mc_questions (id, statement, explanation, category_id) VALUES (4, 'Â¿CuÃ¡les de los siguientes porteros lograron ganar una Copa del Mundo con su selecciÃ³n?', 'Casillas (2010), Buffon (2006) y Neuer (2014) ya son campeones del mundo.', NULL);
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (4, 0, 'Iker Casillas');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (4, 1, 'Gianluigi Buffon');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (4, 2, 'Manuel Neuer');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (4, 3, 'Oliver Kahn');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (4, 4, 'Edwin van der Sar');
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (4, 0);
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (4, 1);
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (4, 2);
INSERT INTO mc_questions (id, statement, explanation, category_id) VALUES (5, 'Selecciona los clubes que compiten en la Premier League de Inglaterra:', 'Roma y Sevilla juegan en Italia y EspaÃ±a respectivamente.', NULL);
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (5, 0, 'Newcastle United');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (5, 1, 'AS Roma');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (5, 2, 'Tottenham Hotspur');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (5, 3, 'Everton');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (5, 4, 'Sevilla');
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (5, 0);
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (5, 2);
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (5, 3);
INSERT INTO mc_questions (id, statement, explanation, category_id) VALUES (6, 'Â¿QuÃ© selecciones nacionales sudamericanas han logrado ganar al menos un Mundial?', 'Solo Brasil, Argentina y Uruguay tienen al menos una estrella de campeÃ³n.', NULL);
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (6, 0, 'Brasil');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (6, 1, 'Colombia');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (6, 2, 'Argentina');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (6, 3, 'Chile');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (6, 4, 'Uruguay');
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (6, 0);
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (6, 2);
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (6, 4);
INSERT INTO mc_questions (id, statement, explanation, category_id) VALUES (7, 'Â¿CuÃ¡les de estos reconocidos jugadores son de nacionalidad espaÃ±ola?', 'Pirlo es italiano y ModriÄ‡ es croata.', NULL);
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (7, 0, 'AndrÃ©s Iniesta');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (7, 1, 'Xavi HernÃ¡ndez');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (7, 2, 'Andrea Pirlo');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (7, 3, 'Sergio Ramos');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (7, 4, 'Luka ModriÄ‡');
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (7, 0);
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (7, 1);
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (7, 3);
INSERT INTO mc_questions (id, statement, explanation, category_id) VALUES (8, 'Selecciona los colores tradicionales de la camiseta del equipo AC Milan:', 'Los apodan I Rossoneri (Los rojo y negros).', NULL);
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (8, 0, 'Rojo');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (8, 1, 'Azul');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (8, 2, 'Negro');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (8, 3, 'Blanco');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (8, 4, 'Amarillo');
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (8, 0);
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (8, 2);
INSERT INTO mc_questions (id, statement, explanation, category_id) VALUES (9, 'Â¿CuÃ¡les de los siguientes torneos corresponden estrictamente a competiciones de selecciones nacionales?', 'La Champions y la Libertadores son competiciones de clubes.', NULL);
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (9, 0, 'Copa AmÃ©rica');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (9, 1, 'UEFA Champions League');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (9, 2, 'Eurocopa');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (9, 3, 'Copa Libertadores');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (9, 4, 'Copa Ãfrica de Naciones');
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (9, 0);
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (9, 2);
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (9, 4);
INSERT INTO mc_questions (id, statement, explanation, category_id) VALUES (10, 'Â¿QuÃ© famosos estadios de fÃºtbol se encuentran ubicados en el Reino Unido?', 'Allianz Arena estÃ¡ en Alemania y Parc des Princes en Francia.', NULL);
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (10, 0, 'Wembley');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (10, 1, 'Anfield');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (10, 2, 'Allianz Arena');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (10, 3, 'Stamford Bridge');
INSERT INTO mc_question_options (question_id, option_index, option_text) VALUES (10, 4, 'Parc des Princes');
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (10, 0);
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (10, 1);
INSERT INTO mc_question_correct_indexes (question_id, correct_index) VALUES (10, 3);
