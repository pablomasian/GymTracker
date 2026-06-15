-- Ejercicios con imágenes representativas (combinando URLs funcionales)
INSERT INTO ejercicio (nombre_ejercicio, descripcion, musculos, equipamiento, estado, imagen_url, bloqueado, tipo_ejercicio) VALUES 
('Bench Press', 'Press de banca con barra', 'Chest, Triceps, Shoulders', 'Barbell', 'APPROVED', 'https://fitnessprogramer.com/wp-content/uploads/2021/02/Barbell-Bench-Press.gif', false, 'STRENGTH'),
('Squat', 'Sentadilla con barra', 'Legs, Glutes', 'Barbell', 'APPROVED', 'https://fitnessprogramer.com/wp-content/uploads/2021/02/BARBELL-SQUAT.gif', false, 'STRENGTH'),
('Deadlift', 'Peso muerto con barra', 'Back, Glutes, Legs', 'Barbell', 'APPROVED', 'https://fitnessprogramer.com/wp-content/uploads/2021/02/Barbell-Deadlift.gif', false, 'STRENGTH'),
('Pull-up', 'Dominadas', 'Back, Biceps', 'Bodyweight', 'APPROVED', 'https://www.inspireusafoundation.org/file/2023/03/scapular-pull-up.gif', false, 'STRENGTH'),
('Shoulder Press', 'Press militar con barra', 'Shoulders, Triceps', 'Barbell', 'APPROVED', 'https://fitnessprogramer.com/wp-content/uploads/2021/02/Dumbbell-Shoulder-Press.gif', false, 'STRENGTH'),
('Barbell Row', 'Remo con barra', 'Back', 'Barbell', 'APPROVED', 'https://fitnessprogramer.com/wp-content/uploads/2021/02/Dumbbell-Row.gif', false, 'STRENGTH'),
('Dumbbell Curl', 'Curl de bíceps con mancuernas', 'Biceps', 'Dumbbell', 'APPROVED', 'https://fitnessprogramer.com/wp-content/uploads/2021/02/Dumbbell-Curl.gif', false, 'STRENGTH'),
('Tricep Dips', 'Fondos de tríceps', 'Triceps, Chest', 'Bodyweight', 'APPROVED', 'https://fitnessprogramer.com/wp-content/uploads/2021/02/Bench-Dips.gif', false, 'STRENGTH'),
('Leg Press', 'Prensa de piernas', 'Legs, Glutes', 'Machine', 'APPROVED', 'https://fitnessprogramer.com/wp-content/uploads/2021/02/Pistol-Squat.gif', false, 'STRENGTH'),
('Lat Pulldown', 'Jalón al pecho', 'Back, Biceps', 'Cable', 'APPROVED', 'https://fitnessprogramer.com/wp-content/uploads/2021/02/Lat-Pulldown.gif', false, 'STRENGTH'),
('Overhead Press', 'Press de hombros con mancuernas', 'Shoulders, Triceps', 'Dumbbell', 'APPROVED', 'https://fitnessprogramer.com/wp-content/uploads/2021/02/Dumbbell-Shoulder-Press.gif', false, 'STRENGTH'),
('Plank', 'Plancha isometrica', 'Core', 'Bodyweight', 'APPROVED', 'https://www.inspireusafoundation.org/file/2022/01/plank.gif', false, 'STRENGTH'),
('Crunch', 'Abdominales crunch', 'Core', 'Bodyweight', 'APPROVED', 'https://www.inspireusafoundation.org/file/2022/01/crunch.gif', false, 'STRENGTH'),
('Lunge', 'Zancadas con mancuernas', 'Legs, Glutes', 'Dumbbell', 'APPROVED', 'https://fitnessprogramer.com/wp-content/uploads/2021/02/Dumbbell-Lunge.gif', false, 'STRENGTH'),
('Burpee', 'Burpee completo', 'Legs, Biceps, Triceps, Glutes, Shoulders, Core', 'Bodyweight', 'APPROVED', 'https://www.inspireusafoundation.org/file/2022/01/burpee-movement.gif', false, 'STRENGTH'),
('Jump Rope', 'Saltar la cuerda', 'Legs, Core', 'Bodyweight', 'APPROVED', 'https://www.inspireusafoundation.org/file/2021/08/jumping-rope-2048x890.png', false, 'STRENGTH');

-- Ejercicios de Cardio (con imágenes)
INSERT INTO ejercicio (nombre_ejercicio, descripcion, musculos, equipamiento, estado, imagen_url, bloqueado, tipo_ejercicio) VALUES 
('Running', 'Correr al aire libre', 'Legs, Cardio', 'None', 'APPROVED', 'https://fitnessprogramer.com/wp-content/uploads/2021/07/Run.gif', false, 'CARDIO'),
('Treadmill Running', 'Correr en cinta', 'Legs, Cardio', 'Treadmill', 'APPROVED', 'https://fitnessprogramer.com/wp-content/uploads/2021/06/Treadmill-.gif', false, 'CARDIO'),
('Cycling', 'Ciclismo', 'Legs, Cardio', 'Bicycle', 'APPROVED', 'https://fitnessprogramer.com/wp-content/uploads/2022/02/Stationary-Bike-Run.gif', false, 'CARDIO'),
('Rowing Machine', 'Remo en máquina', 'Back, Arms, Cardio', 'Rowing Machine', 'APPROVED', 'https://fitnessprogramer.com/wp-content/uploads/2021/06/Rowing-Machine.gif', false, 'CARDIO'),
('Elliptical Trainer', 'Entrenamiento en elíptica', 'Full Body, Cardio', 'Elliptical', 'APPROVED', 'https://fitnessprogramer.com/wp-content/uploads/2021/10/Elliptical-Machine.gif', false, 'CARDIO');

-- BCrypt hash de '123456' (cost 10). Puedes regenerar si cambias encoder.
INSERT INTO usuario_GymTracker (nombre_usuario, contrasena_usuario, rol, username_usuario, avatar_url, bloqueado)
VALUES ('Coach', '$2a$10$wnY0Ax7AnCM1jgpMev1V1O9uX6cR9mPiXr0V6YJp23iOe6vAUFeZK', 'COACH', 'coach', NULL, FALSE);

INSERT INTO usuario_GymTracker (nombre_usuario, contrasena_usuario, rol, username_usuario, avatar_url, bloqueado)
VALUES ('User', '$2a$10$wnY0Ax7AnCM1jgpMev1V1O9uX6cR9mPiXr0V6YJp23iOe6vAUFeZK', 'USER', 'user', NULL, FALSE);

INSERT INTO usuario_GymTracker (nombre_usuario, contrasena_usuario, rol, username_usuario, avatar_url, bloqueado)
VALUES ('Admin', '$2a$10$84810YbiITzw2xHwKqFE/uumuTEqopcbbL.39fxGFg8r2ze0IWzGO', 'ADMIN', 'admin', NULL, FALSE);

-- Rutinas de Cardio (creadas por el Coach, id=1)
INSERT INTO rutinas (nombre_rutina, id_usuario, visible, estado, bloqueado) VALUES
('Cardio Básico', 1, true, 'APPROVED', false),
('Cardio Intenso', 1, true, 'APPROVED', false),
('Full Body Mix', 1, true, 'APPROVED', false);

-- Ejercicios de la rutina Cardio Básico (rutina id=1, usa ejercicios de cardio: Running=17, Cycling=19, Elliptical=21)
INSERT INTO rutina_ejercicio (id_rutina, id_ejercicio, num_series, num_repeticiones, peso, distancia_objetivo, duracion_objetivo) VALUES
(1, 17, 1, 0, NULL, 3.00, 20),
(1, 19, 1, 0, NULL, 5.00, 15),
(1, 21, 1, 0, NULL, 2.00, 10);

-- Ejercicios de la rutina Cardio Intenso (rutina id=2, usa: Treadmill=18, Rowing=20, Running=17)
INSERT INTO rutina_ejercicio (id_rutina, id_ejercicio, num_series, num_repeticiones, peso, distancia_objetivo, duracion_objetivo) VALUES
(2, 18, 1, 0, NULL, 5.00, 25),
(2, 20, 1, 0, NULL, 3.00, 15),
(2, 17, 1, 0, NULL, 5.00, 30);

-- Ejercicios de la rutina Full Body Mix (rutina id=3, mezcla fuerza y cardio)
-- Fuerza: Bench Press=1, Squat=2, Deadlift=3
-- Cardio: Running=17, Cycling=19
INSERT INTO rutina_ejercicio (id_rutina, id_ejercicio, num_series, num_repeticiones, peso, distancia_objetivo, duracion_objetivo) VALUES
(3, 1, 4, 10, 60.00, NULL, NULL),
(3, 2, 4, 8, 80.00, NULL, NULL),
(3, 17, 1, 0, NULL, 2.00, 15),
(3, 3, 3, 6, 100.00, NULL, NULL),
(3, 19, 1, 0, NULL, 3.00, 10);
