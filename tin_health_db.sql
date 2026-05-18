-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 18, 2026 at 3:58 PM - Macoy
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

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
-- Table structure for table `Activities`
--

CREATE TABLE `Activities` (
  `act_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `activity_type_id` int(11) NOT NULL,
  `quantity` decimal(8,2) NOT NULL,
  `calories` decimal(8,2) DEFAULT NULL,
  `time` varchar(20) NOT NULL DEFAULT '',
  `log_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Activities`
--

INSERT INTO `Activities` (`act_id`, `user_id`, `activity_type_id`, `quantity`, `calories`, `time`, `log_date`) VALUES
(1, 1, 2, 30.00, 280.00, '15:00', '2026-05-04'),
(2, 1, 5, 45.00, 200.00, '15:00', '2026-05-04'),
(3, 2, 1, 60.00, 180.00, '15:00', '2026-05-04'),
(4, 1, 3, 65.00, 234.00, '22:30', '2026-05-05'),
(5, 1, 13, 65.00, 234.00, '12:34', '2026-05-16'),
(6, 1, 46, 40.00, 100.00, '12:35', '2026-05-16'),
(9, 1, 1, 30.00, 136.34, '17:55', '2026-05-17');

-- --------------------------------------------------------

--
-- Table structure for table `ActivityTypes`
--

CREATE TABLE `ActivityTypes` (
  `activity_type_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `default_unit` varchar(20) DEFAULT 'minutes',
  `met_value` decimal(5,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `ActivityTypes`
--

INSERT INTO `ActivityTypes` (`activity_type_id`, `name`, `default_unit`, `met_value`) VALUES
(1, 'Walking', 'minutes', 3.50),
(2, 'Running', 'minutes', 8.00),
(3, 'Cycling', 'minutes', 6.00),
(4, 'Swimming', 'minutes', 7.00),
(5, 'Weight Training', 'minutes', 5.00),
(6, 'Run', 'minutes', 9.80),
(7, 'Treadmill', 'minutes', 8.00),
(8, 'Indoor Track', 'minutes', 8.00),
(9, 'Trail Run', 'minutes', 9.00),
(10, 'Virtual Run', 'minutes', 8.00),
(11, 'Ultra Run', 'minutes', 8.50),
(12, 'Track Run', 'minutes', 10.00),
(13, 'Bike', 'minutes', 7.50),
(14, 'Bike Indoor', 'minutes', 7.00),
(15, 'Mountain Bike (MTB)', 'minutes', 8.50),
(16, 'eBike', 'minutes', 3.00),
(17, 'eMTB', 'minutes', 4.00),
(18, 'Cyclocross', 'minutes', 9.00),
(19, 'Gravel Bike', 'minutes', 8.00),
(20, 'Bike Commute', 'minutes', 6.00),
(21, 'Road Bike', 'minutes', 8.50),
(22, 'BMX', 'minutes', 8.50),
(23, 'Strength', 'minutes', 6.00),
(24, 'Cardio', 'minutes', 7.00),
(25, 'Elliptical', 'minutes', 5.00),
(26, 'Stair Stepper', 'minutes', 9.00),
(27, 'Floor Climb', 'minutes', 8.00),
(28, 'Row Indoor', 'minutes', 7.00),
(29, 'HIIT', 'minutes', 8.50),
(30, 'Yoga', 'minutes', 2.50),
(31, 'Pilates', 'minutes', 3.50),
(32, 'Breathwork', 'minutes', 1.30),
(33, 'Jump Rope', 'minutes', 10.00),
(34, 'Mobility', 'minutes', 2.50),
(35, 'Indoor Bouldering', 'minutes', 7.00),
(36, 'Hike', 'minutes', 6.50),
(37, 'Walk', 'minutes', 3.50),
(38, 'Walk Indoor', 'minutes', 3.00),
(39, 'Climb', 'minutes', 8.00),
(40, 'Bouldering', 'minutes', 7.50),
(41, 'Mountaineering', 'minutes', 8.50),
(42, 'Rucking', 'minutes', 8.00),
(43, 'Disc Golf', 'minutes', 3.00),
(44, 'Archery', 'minutes', 3.50),
(45, 'Horseback Riding', 'minutes', 5.50),
(46, 'Expedition', 'minutes', 5.00),
(47, 'Pool Swim', 'minutes', 7.00),
(48, 'Open Water Swim', 'minutes', 7.50),
(49, 'Stand Up Paddle (SUP)', 'minutes', 6.00),
(50, 'Row', 'minutes', 5.00),
(51, 'Kayak', 'minutes', 5.00),
(52, 'Surfing', 'minutes', 3.00),
(53, 'Kiteboard', 'minutes', 5.50),
(54, 'Windsurf', 'minutes', 5.00),
(55, 'Water Ski', 'minutes', 6.00),
(56, 'Wakeboard', 'minutes', 6.00),
(57, 'Whitewater', 'minutes', 6.50),
(58, 'Sail', 'minutes', 3.00),
(59, 'Fishing', 'minutes', 3.50),
(60, 'Ski', 'minutes', 7.00),
(61, 'Snowboard', 'minutes', 7.00),
(62, 'XC Classic Ski', 'minutes', 8.00),
(63, 'XC Skate Ski', 'minutes', 9.00),
(64, 'Backcountry Ski', 'minutes', 8.50),
(65, 'Ice Skating', 'minutes', 7.00),
(66, 'Snowshoe', 'minutes', 8.00),
(67, 'Tennis', 'minutes', 7.30),
(68, 'Pickleball', 'minutes', 6.00),
(69, 'Padel', 'minutes', 6.50),
(70, 'Squash', 'minutes', 7.30),
(71, 'Badminton', 'minutes', 5.50),
(72, 'Table Tennis', 'minutes', 4.00),
(73, 'Basketball', 'minutes', 8.00),
(74, 'Volleyball', 'minutes', 6.00),
(75, 'Soccer', 'minutes', 8.00),
(76, 'American Football', 'minutes', 8.00),
(77, 'Lacrosse', 'minutes', 8.00),
(78, 'Rugby', 'minutes', 8.50),
(79, 'Cricket', 'minutes', 4.80),
(80, 'Baseball', 'minutes', 5.00),
(81, 'Softball', 'minutes', 5.00),
(82, 'Motorcycle', 'minutes', 2.00),
(83, 'ATV', 'minutes', 2.50),
(84, 'Snowmobile', 'minutes', 2.50),
(85, 'Tactical', 'minutes', 5.00),
(86, 'Jumpmaster', 'minutes', 3.50);

-- --------------------------------------------------------

--
-- Table structure for table `ComboItems`
--

CREATE TABLE `ComboItems` (
  `comboitems_id` int(11) NOT NULL,
  `combo_id` int(11) NOT NULL,
  `consumable_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `ComboItems`
--

INSERT INTO `ComboItems` (`comboitems_id`, `combo_id`, `consumable_id`) VALUES
(1, 7, 4),
(2, 7, 5),
(9, 16, 17),
(10, 16, 18);

-- --------------------------------------------------------

--
-- Table structure for table `Consumables`
--

CREATE TABLE `Consumables` (
  `consumable_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `type` enum('food','foodcombo') NOT NULL,
  `is_pending` tinyint(1) DEFAULT 0,
  `nutri_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Consumables`
--

INSERT INTO `Consumables` (`consumable_id`, `name`, `type`, `is_pending`, `nutri_id`) VALUES
(1, 'Apple', 'food', 0, 1),
(2, 'Cheeseburger', 'food', 0, 2),
(3, 'Grilled Chicken', 'food', 0, 3),
(4, 'Oreo', 'food', 0, 4),
(5, 'Milk', 'food', 0, 5),
(6, 'Water', 'food', 0, 6),
(7, 'Oreo with Milk', 'foodcombo', 0, NULL),
(8, 'FRIED CHICKEN', 'food', 0, 7),
(9, 'CINNAMON BREAD', 'food', 0, 8),
(10, 'BURGER', 'food', 0, 9),
(11, 'ORANGE', 'food', 0, 10),
(12, 'Fish, bass, fried', 'food', 0, 11),
(13, 'Hamburger And Cheese', 'foodcombo', 0, NULL),
(14, 'Hotdog With Rice', 'foodcombo', 0, NULL),
(15, 'Fried Chicken With Rice', 'foodcombo', 0, NULL),
(16, 'Egg With Rice', 'foodcombo', 0, NULL),
(17, 'Eggs, Grade A, Large, egg white', 'food', 0, 18),
(18, 'Rice crackers', 'food', 0, 19),
(19, 'Bananas, overripe, raw', 'food', 0, 20),
(20, 'Chicken, meatless, breaded, fried', 'food', 0, 21),
(21, 'Restaurant, Chinese, fried rice, without meat', 'food', 0, 22),
(31, 'Almond butter, creamy', 'food', 0, 32);

-- --------------------------------------------------------

--
-- Table structure for table `Meals`
--

CREATE TABLE `Meals` (
  `meal_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `consumable_id` int(11) NOT NULL,
  `meal_type` enum('Breakfast','Lunch','Dinner','Snack') NOT NULL,
  `serving_size` decimal(8,2) NOT NULL,
  `serving_units` varchar(20) NOT NULL,
  `time` varchar(20) NOT NULL DEFAULT '',
  `log_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Meals`
--

INSERT INTO `Meals` (`meal_id`, `user_id`, `consumable_id`, `meal_type`, `serving_size`, `serving_units`, `time`, `log_date`) VALUES
(1, 1, 2, 'Lunch', 1.00, 'piece', '15:00', '2026-05-04'),
(2, 1, 1, 'Snack', 1.00, 'piece', '15:00', '2026-05-04'),
(3, 2, 3, 'Dinner', 1.50, 'serving', '15:00', '2026-05-04'),
(4, 2, 7, 'Snack', 1.00, 'bowl', '15:00', '2026-05-04'),
(5, 1, 8, 'Dinner', 1.00, 'serving', '22:57', '2026-05-15'),
(6, 1, 9, 'Breakfast', 1.00, 'serving', '11:41', '2026-05-16'),
(7, 1, 10, 'Snack', 1.00, 'serving', '11:42', '2026-05-16'),
(8, 1, 1, 'Breakfast', 1.00, 'serving', '13:45', '2026-05-16'),
(9, 1, 1, 'Breakfast', 1.00, 'serving', '13:46', '2026-05-16'),
(11, 1, 12, 'Lunch', 1.00, 'serving', '13:46', '2026-05-16'),
(13, 1, 13, 'Lunch', 1.00, 'serving', '21:29', '2026-05-16'),
(14, 1, 14, 'Lunch', 1.00, 'serving', '21:37', '2026-05-16'),
(15, 1, 15, 'Dinner', 1.00, 'serving', '21:58', '2026-05-16'),
(16, 1, 16, 'Lunch', 1.00, 'serving', '22:08', '2026-05-16'),
(17, 1, 14, 'Lunch', 1.00, 'serving', '01:11', '2026-05-17'),
(18, 1, 15, 'Dinner', 1.00, 'serving', '10:49', '2026-05-17'),
(19, 1, 19, 'Dinner', 1.00, 'serving', '11:43', '2026-05-17'),
(22, 1, 20, 'Lunch', 1.00, 'serving', '20:54', '2026-05-17'),
(32, 1, 31, 'Snack', 1.00, 'serving', '23:57', '2026-05-17');

-- --------------------------------------------------------

--
-- Table structure for table `NutritionalDetails`
--

CREATE TABLE `NutritionalDetails` (
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
-- Dumping data for table `NutritionalDetails`
--

INSERT INTO `NutritionalDetails` (`nutri_id`, `sodium`, `carbs`, `sugar`, `fiber`, `calories`, `cholesterol`, `protein`, `fats`) VALUES
(1, 8.50, 30.20, 1.20, 3.50, 150.00, 5.00, 5.20, 2.10),
(2, 210.00, 27.00, 3.00, 1.00, 240.00, 30.00, 8.00, 10.00),
(3, 160.00, 22.00, 2.50, 2.00, 180.00, 15.00, 12.00, 6.00),
(4, 6.00, 37.00, 33.00, 2.00, 150.00, 0.00, 3.00, 0.50),
(5, 50.00, 12.00, 12.00, 0.00, 120.00, 10.00, 8.00, 5.00),
(6, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00),
(7, 726.00, 9.52, 0.00, 0.00, 274.00, 48.00, 13.10, 17.90),
(8, 421.00, 52.60, 0.00, 2.60, 316.00, 13.00, 7.89, 7.89),
(9, 3180.00, 23.50, 0.00, 5.90, 88.00, 0.00, 0.00, 0.00),
(10, 381.00, 90.50, 0.00, 0.00, 381.00, 0.00, 4.76, 0.00),
(11, 395.00, 11.72, 0.00, 0.50, 238.00, 37.00, 15.93, 13.69),
(12, 469.30, 28.77, 5.73, 1.23, 250.80, 25.65, 12.27, 9.59),
(13, 360.00, 21.52, 1.00, 1.01, 195.84, 4.80, 5.00, 10.00),
(14, 140.05, 14.21, 2.06, 0.51, 79.10, 0.00, 2.77, 1.11),
(15, 233.00, 82.60, 0.00, 0.00, 416.00, 0.00, 10.00, 5.00),
(16, 400.00, 8.51, 0.00, 4.30, 234.00, 0.00, 21.30, 12.80),
(17, 233.00, 82.60, 0.00, 0.00, 416.00, 0.00, 10.00, 5.00),
(18, 0.00, 2.36, 0.00, 0.00, 55.00, 0.00, 10.70, 0.00),
(19, 233.00, 82.60, 0.00, 0.00, 416.00, 0.00, 10.00, 5.00),
(20, 0.00, 22.11, 0.00, 1.87, 93.50, 0.00, 0.80, 0.24),
(21, 144.00, 3.06, 0.00, 1.55, 84.24, 0.00, 7.66, 4.60),
(22, 361.00, 32.50, 0.00, 0.00, 174.00, 0.00, 3.84, 3.19),
(23, 140.05, 14.21, 2.06, 0.51, 79.10, 0.00, 2.77, 1.11),
(24, 140.05, 14.21, 2.06, 0.51, 79.10, 0.00, 2.77, 1.11),
(25, 140.05, 14.21, 2.06, 0.51, 79.10, 0.00, 2.77, 1.11),
(26, 140.05, 14.21, 2.06, 0.51, 79.10, 0.00, 2.77, 1.11),
(27, 140.05, 14.21, 2.06, 0.51, 79.10, 0.00, 2.77, 1.11),
(28, 140.05, 14.21, 2.06, 0.51, 79.10, 0.00, 2.77, 1.11),
(29, 140.05, 14.21, 2.06, 0.51, 79.10, 0.00, 2.77, 1.11),
(30, 1.00, 21.24, 0.00, 9.72, 602.51, 0.00, 20.79, 53.04),
(31, 140.05, 14.21, 2.06, 0.51, 79.10, 0.00, 2.77, 1.11),
(32, 1.00, 21.24, 0.00, 9.72, 602.51, 0.00, 20.79, 53.04);

-- --------------------------------------------------------

--
-- Table structure for table `UserPrefs`
--

CREATE TABLE `UserPrefs` (
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
  `exercise_intensity` int(11) DEFAULT 2
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `UserPrefs`
--

INSERT INTO `UserPrefs` (`userpref_id`, `user_id`, `goal`, `target_weight_kg`, `enable_exercise_prompts`, `prompt_freq`, `theme`, `exercise_reminders`, `meal_reminders`, `achievement_notifications`, `daily_calorie_in`, `daily_calorie_out`, `weekly_activity_goal`, `exercise_intensity`) VALUES
(1, 1, 'Lose', 70.00, 1, 45, 'Dark', 1, 1, 1, 2000, 500, 3, 2),
(2, 2, 'Maintain', NULL, 1, 30, 'Light', 1, 1, 1, 1800, 300, 3, 2);

-- --------------------------------------------------------

--
-- Table structure for table `Users`
--

CREATE TABLE `Users` (
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
-- Dumping data for table `Users`
--

INSERT INTO `Users` (`user_id`, `fullname`, `username`, `email`, `password_hash`, `age`, `date_of_birth`, `gender`, `weight_kg`, `height_cm`, `activity_level`) VALUES
(1, 'John Doe', '123', 'john@example.com', '123', 28, '1995-03-15', 'Male', 75.50, 178.00, 'Extremely Active'),
(2, 'Jane Smith', 'janesmith', 'jane@example.com', 'hashed_pw_2', 24, '1999-07-22', 'Female', 62.30, 165.00, 'Lightly');

-- --------------------------------------------------------

--
-- Table structure for table `WeightHistories`
--

CREATE TABLE `WeightHistories` (
  `weight_log_id` int(11) NOT NULL,
  `weight_kg` decimal(5,2) NOT NULL,
  `log_date` date NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `WeightHistories`
--

INSERT INTO `WeightHistories` (`weight_log_id`, `weight_kg`, `log_date`, `user_id`) VALUES
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
-- Indexes for table `Activities`
--
ALTER TABLE `Activities`
  ADD PRIMARY KEY (`act_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `activity_type_id` (`activity_type_id`);

--
-- Indexes for table `ActivityTypes`
--
ALTER TABLE `ActivityTypes`
  ADD PRIMARY KEY (`activity_type_id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `ComboItems`
--
ALTER TABLE `ComboItems`
  ADD PRIMARY KEY (`comboitems_id`),
  ADD KEY `combo_id` (`combo_id`),
  ADD KEY `consumable_id` (`consumable_id`);

--
-- Indexes for table `Consumables`
--
ALTER TABLE `Consumables`
  ADD PRIMARY KEY (`consumable_id`),
  ADD KEY `nutri_id` (`nutri_id`);

--
-- Indexes for table `Meals`
--
ALTER TABLE `Meals`
  ADD PRIMARY KEY (`meal_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `consumable_id` (`consumable_id`);

--
-- Indexes for table `NutritionalDetails`
--
ALTER TABLE `NutritionalDetails`
  ADD PRIMARY KEY (`nutri_id`);

--
-- Indexes for table `UserPrefs`
--
ALTER TABLE `UserPrefs`
  ADD PRIMARY KEY (`userpref_id`),
  ADD UNIQUE KEY `user_id` (`user_id`);

--
-- Indexes for table `Users`
--
ALTER TABLE `Users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `WeightHistories`
--
ALTER TABLE `WeightHistories`
  ADD PRIMARY KEY (`weight_log_id`),
  ADD UNIQUE KEY `unique_daily_weight` (`user_id`,`log_date`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `Activities`
--
ALTER TABLE `Activities`
  MODIFY `act_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `ActivityTypes`
--
ALTER TABLE `ActivityTypes`
  MODIFY `activity_type_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=87;

--
-- AUTO_INCREMENT for table `ComboItems`
--
ALTER TABLE `ComboItems`
  MODIFY `comboitems_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `Consumables`
--
ALTER TABLE `Consumables`
  MODIFY `consumable_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT for table `Meals`
--
ALTER TABLE `Meals`
  MODIFY `meal_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- AUTO_INCREMENT for table `NutritionalDetails`
--
ALTER TABLE `NutritionalDetails`
  MODIFY `nutri_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- AUTO_INCREMENT for table `UserPrefs`
--
ALTER TABLE `UserPrefs`
  MODIFY `userpref_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `Users`
--
ALTER TABLE `Users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `WeightHistories`
--
ALTER TABLE `WeightHistories`
  MODIFY `weight_log_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `Activities`
--
ALTER TABLE `Activities`
  ADD CONSTRAINT `activities_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`),
  ADD CONSTRAINT `activities_ibfk_2` FOREIGN KEY (`activity_type_id`) REFERENCES `ActivityTypes` (`activity_type_id`);

--
-- Constraints for table `ComboItems`
--
ALTER TABLE `ComboItems`
  ADD CONSTRAINT `comboitems_ibfk_1` FOREIGN KEY (`combo_id`) REFERENCES `Consumables` (`consumable_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `comboitems_ibfk_2` FOREIGN KEY (`consumable_id`) REFERENCES `Consumables` (`consumable_id`);

--
-- Constraints for table `Consumables`
--
ALTER TABLE `Consumables`
  ADD CONSTRAINT `consumables_ibfk_1` FOREIGN KEY (`nutri_id`) REFERENCES `NutritionalDetails` (`nutri_id`);

--
-- Constraints for table `Meals`
--
ALTER TABLE `Meals`
  ADD CONSTRAINT `meals_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`),
  ADD CONSTRAINT `meals_ibfk_2` FOREIGN KEY (`consumable_id`) REFERENCES `Consumables` (`consumable_id`);

--
-- Constraints for table `UserPrefs`
--
ALTER TABLE `UserPrefs`
  ADD CONSTRAINT `userprefs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `WeightHistories`
--
ALTER TABLE `WeightHistories`
  ADD CONSTRAINT `weighthistories_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
