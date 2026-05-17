-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 17, 2026 at 02:35 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `tin_health_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `activities`
--

CREATE TABLE `activities` (
                              `act_id` int(11) NOT NULL,
                              `user_id` int(11) NOT NULL,
                              `activity_type_id` int(11) NOT NULL,
                              `quantity` decimal(8,2) NOT NULL,
                              `calories` decimal(8,2) DEFAULT NULL,
                              `log_timestamp` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `activities`
--

INSERT INTO `activities` (`act_id`, `user_id`, `activity_type_id`, `quantity`, `calories`, `log_timestamp`) VALUES
                                                                                                                (1, 1, 2, 30.00, 280.00, '2026-05-04 07:00:00'),
                                                                                                                (2, 1, 5, 45.00, 200.00, '2026-05-04 07:00:00'),
                                                                                                                (3, 2, 1, 60.00, 180.00, '2026-05-04 07:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `activitytypes`
--

CREATE TABLE `activitytypes` (
                                 `activity_type_id` int(11) NOT NULL,
                                 `name` varchar(100) NOT NULL,
                                 `default_unit` varchar(20) DEFAULT 'minutes',
                                 `met_value` decimal(5,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `activitytypes`
--

INSERT INTO `activitytypes` (`activity_type_id`, `name`, `default_unit`, `met_value`) VALUES
                                                                                          (1, 'Walking', 'minutes', 3.50),
                                                                                          (2, 'Running', 'minutes', 8.00),
                                                                                          (3, 'Cycling', 'minutes', 6.00),
                                                                                          (4, 'Swimming', 'minutes', 7.00),
                                                                                          (5, 'Weight Training', 'minutes', 5.00);

-- --------------------------------------------------------

--
-- Table structure for table `comboitems`
--

CREATE TABLE `comboitems` (
                              `comboitems_id` int(11) NOT NULL,
                              `combo_id` int(11) NOT NULL,
                              `consumable_id` int(11) NOT NULL,
                              `quantity` decimal(8,2) DEFAULT 1.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `comboitems`
--

INSERT INTO `comboitems` (`comboitems_id`, `combo_id`, `consumable_id`, `quantity`) VALUES
                                                                                        (1, 7, 4, 3.00),
                                                                                        (2, 7, 5, 1.00);

-- --------------------------------------------------------

--
-- Table structure for table `consumables`
--

CREATE TABLE `consumables` (
                               `consumable_id` int(11) NOT NULL,
                               `name` varchar(100) NOT NULL,
                               `type` enum('food','foodcombo') NOT NULL,
                               `is_pending` tinyint(1) DEFAULT 0,
                               `nutri_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `consumables`
--

INSERT INTO `consumables` (`consumable_id`, `name`, `type`, `is_pending`, `nutri_id`) VALUES
                                                                                          (1, 'Apple', 'food', 0, 1),
                                                                                          (2, 'Cheeseburger', 'food', 0, 2),
                                                                                          (3, 'Grilled Chicken', 'food', 0, 3),
                                                                                          (4, 'Oreo', 'food', 0, 4),
                                                                                          (5, 'Milk', 'food', 0, 5),
                                                                                          (6, 'Water', 'food', 0, 6),
                                                                                          (7, 'Oreo with Milk', 'foodcombo', 0, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `meals`
--

CREATE TABLE `meals` (
                         `meal_id` int(11) NOT NULL,
                         `user_id` int(11) NOT NULL,
                         `consumable_id` int(11) NOT NULL,
                         `meal_type` enum('Breakfast','Lunch','Dinner','Snack') NOT NULL,
                         `serving_size` decimal(8,2) NOT NULL,
                         `serving_units` varchar(20) NOT NULL,
                         `log_timestamp` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `meals`
--

INSERT INTO `meals` (`meal_id`, `user_id`, `consumable_id`, `meal_type`, `serving_size`, `serving_units`, `log_timestamp`) VALUES
                                                                                                                               (1, 1, 2, 'Lunch', 1.00, 'piece', '2026-05-04 07:00:00'),
                                                                                                                               (2, 1, 1, 'Snack', 1.00, 'piece', '2026-05-04 07:00:00'),
                                                                                                                               (3, 2, 3, 'Dinner', 1.50, 'serving', '2026-05-04 07:00:00'),
                                                                                                                               (4, 2, 7, 'Snack', 1.00, 'bowl', '2026-05-04 07:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `nutritionaldetails`
--

CREATE TABLE `nutritionaldetails` (
                                      `nutri_id` int(11) NOT NULL,
                                      `sodium` decimal(8,2) DEFAULT NULL,
                                      `carbs` decimal(8,2) DEFAULT NULL,
                                      `sugar` decimal(8,2) DEFAULT NULL,
                                      `fiber` decimal(8,2) DEFAULT NULL,
                                      `calories` decimal(8,2) DEFAULT NULL,
                                      `cholesterol` decimal(8,2) DEFAULT NULL,
                                      `protein` decimal(8,2) DEFAULT NULL,
                                      `fats` decimal(8,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `nutritionaldetails`
--

INSERT INTO `nutritionaldetails` (`nutri_id`, `sodium`, `carbs`, `sugar`, `fiber`, `calories`, `cholesterol`, `protein`, `fats`) VALUES
                                                                                                                                     (1, 8.50, 30.20, 1.20, 3.50, 150.00, 5.00, 5.20, 2.10),
                                                                                                                                     (2, 210.00, 27.00, 3.00, 1.00, 240.00, 30.00, 8.00, 10.00),
                                                                                                                                     (3, 160.00, 22.00, 2.50, 2.00, 180.00, 15.00, 12.00, 6.00),
                                                                                                                                     (4, 6.00, 37.00, 33.00, 2.00, 150.00, 0.00, 3.00, 0.50),
                                                                                                                                     (5, 50.00, 12.00, 12.00, 0.00, 120.00, 10.00, 8.00, 5.00),
                                                                                                                                     (6, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00);

-- --------------------------------------------------------

--
-- Table structure for table `userprefs`
--

CREATE TABLE `userprefs` (
                             `userpref_id` int(11) NOT NULL,
                             `user_id` int(11) NOT NULL,
                             `goal` enum('Lose','Gain','Maintain','Build Muscle') NOT NULL,
                             `target_weight_kg` decimal(5,2) DEFAULT NULL,
                             `enable_exercise_prompts` tinyint(1) DEFAULT 1,
                             `prompt_freq` int(11) DEFAULT 30,
                             `theme` enum('Light','Dark','System') DEFAULT 'Light',
                             `exercise_reminders` tinyint(1) DEFAULT 1,
                             `meal_reminders` tinyint(1) DEFAULT 1,
                             `achievement_notifications` tinyint(1) DEFAULT 1,
                             `daily_calorie_in` int(11) DEFAULT NULL,
                             `daily_calorie_out` int(11) DEFAULT NULL,
                             `weekly_activity_goal` int(11) DEFAULT 3,
                             `exercise_intensity` int(11) DEFAULT 5
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `userprefs`
--

INSERT INTO `userprefs` (`userpref_id`, `user_id`, `goal`, `target_weight_kg`, `enable_exercise_prompts`, `prompt_freq`, `theme`, `exercise_reminders`, `meal_reminders`, `achievement_notifications`, `daily_calorie_in`, `daily_calorie_out`, `weekly_activity_goal`, `exercise_intensity`) VALUES
                                                                                                                                                                                                                                                                                                  (1, 1, 'Lose', 70.00, 1, 45, 'Dark', 1, 1, 1, 2000, 500, 3, 5),
                                                                                                                                                                                                                                                                                                  (2, 2, 'Maintain', NULL, 1, 30, 'Light', 1, 1, 1, 1800, 300, 3, 5);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
                         `user_id` int(11) NOT NULL,
                         `fullname` varchar(100) NOT NULL,
                         `username` varchar(50) NOT NULL,
                         `email` varchar(100) NOT NULL,
                         `password_hash` varchar(255) NOT NULL,
                         `age` int(11) DEFAULT NULL,
                         `date_of_birth` date DEFAULT NULL,
                         `gender` enum('Male','Female','Other') DEFAULT NULL,
                         `weight_kg` decimal(5,2) DEFAULT NULL,
                         `height_cm` decimal(5,2) DEFAULT NULL,
                         `activity_level` enum('Sedentary','Lightly','Moderately','Very Active','Extremely Active') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `fullname`, `username`, `email`, `password_hash`, `age`, `date_of_birth`, `gender`, `weight_kg`, `height_cm`, `activity_level`) VALUES
                                                                                                                                                                    (1, 'John Doe', '123', 'john@example.com', '123', 28, '1995-03-15', 'Male', 75.50, 178.00, 'Extremely Active'),
                                                                                                                                                                    (2, 'Jane Smith', 'janesmith', 'jane@example.com', 'hashed_pw_2', 24, '1999-07-22', 'Female', 62.30, 165.00, 'Lightly');

-- --------------------------------------------------------

--
-- Table structure for table `weighthistories`
--

CREATE TABLE `weighthistories` (
                                   `weight_log_id` int(11) NOT NULL,
                                   `weight_kg` decimal(5,2) NOT NULL,
                                   `log_date` date NOT NULL,
                                   `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `weighthistories`
--

INSERT INTO `weighthistories` (`weight_log_id`, `weight_kg`, `log_date`, `user_id`) VALUES
                                                                                        (1, 75.50, '2025-05-01', 1),
                                                                                        (2, 74.80, '2025-05-08', 1),
                                                                                        (3, 74.20, '2025-05-15', 1),
                                                                                        (4, 62.30, '2025-05-01', 2),
                                                                                        (5, 62.50, '2025-05-08', 2),
                                                                                        (6, 62.20, '2025-05-15', 2);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `activities`
--
ALTER TABLE `activities`
    ADD PRIMARY KEY (`act_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `activity_type_id` (`activity_type_id`);

--
-- Indexes for table `activitytypes`
--
ALTER TABLE `activitytypes`
    ADD PRIMARY KEY (`activity_type_id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `comboitems`
--
ALTER TABLE `comboitems`
    ADD PRIMARY KEY (`comboitems_id`),
  ADD KEY `combo_id` (`combo_id`),
  ADD KEY `consumable_id` (`consumable_id`);

--
-- Indexes for table `consumables`
--
ALTER TABLE `consumables`
    ADD PRIMARY KEY (`consumable_id`),
  ADD KEY `nutri_id` (`nutri_id`);

--
-- Indexes for table `meals`
--
ALTER TABLE `meals`
    ADD PRIMARY KEY (`meal_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `consumable_id` (`consumable_id`);

--
-- Indexes for table `nutritionaldetails`
--
ALTER TABLE `nutritionaldetails`
    ADD PRIMARY KEY (`nutri_id`);

--
-- Indexes for table `userprefs`
--
ALTER TABLE `userprefs`
    ADD PRIMARY KEY (`userpref_id`),
  ADD UNIQUE KEY `user_id` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
    ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `weighthistories`
--
ALTER TABLE `weighthistories`
    ADD PRIMARY KEY (`weight_log_id`),
  ADD UNIQUE KEY `unique_daily_weight` (`user_id`,`log_date`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `activities`
--
ALTER TABLE `activities`
    MODIFY `act_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `activitytypes`
--
ALTER TABLE `activitytypes`
    MODIFY `activity_type_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `comboitems`
--
ALTER TABLE `comboitems`
    MODIFY `comboitems_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `consumables`
--
ALTER TABLE `consumables`
    MODIFY `consumable_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `meals`
--
ALTER TABLE `meals`
    MODIFY `meal_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `nutritionaldetails`
--
ALTER TABLE `nutritionaldetails`
    MODIFY `nutri_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `userprefs`
--
ALTER TABLE `userprefs`
    MODIFY `userpref_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
    MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `weighthistories`
--
ALTER TABLE `weighthistories`
    MODIFY `weight_log_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `activities`
--
ALTER TABLE `activities`
    ADD CONSTRAINT `activities_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `activities_ibfk_2` FOREIGN KEY (`activity_type_id`) REFERENCES `activitytypes` (`activity_type_id`);

--
-- Constraints for table `comboitems`
--
ALTER TABLE `comboitems`
    ADD CONSTRAINT `comboitems_ibfk_1` FOREIGN KEY (`combo_id`) REFERENCES `consumables` (`consumable_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `comboitems_ibfk_2` FOREIGN KEY (`consumable_id`) REFERENCES `consumables` (`consumable_id`);

--
-- Constraints for table `consumables`
--
ALTER TABLE `consumables`
    ADD CONSTRAINT `consumables_ibfk_1` FOREIGN KEY (`nutri_id`) REFERENCES `nutritionaldetails` (`nutri_id`);

--
-- Constraints for table `meals`
--
ALTER TABLE `meals`
    ADD CONSTRAINT `meals_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `meals_ibfk_2` FOREIGN KEY (`consumable_id`) REFERENCES `consumables` (`consumable_id`);

--
-- Constraints for table `userprefs`
--
ALTER TABLE `userprefs`
    ADD CONSTRAINT `userprefs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `weighthistories`
--
ALTER TABLE `weighthistories`
    ADD CONSTRAINT `weighthistories_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;