-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 26, 2025 at 08:27 AM
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
-- Database: `ticketing_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `maskapai`
--

CREATE TABLE `maskapai` (
  `id` int(11) NOT NULL,
  `nama_maskapai` varchar(255) NOT NULL,
  `kode_maskapai` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `maskapai`
--

INSERT INTO `maskapai` (`id`, `nama_maskapai`, `kode_maskapai`) VALUES
(1, 'Garuda Indonesia', 'GA'),
(2, 'Lion Air', 'JT'),
(3, 'Citilink', 'QG'),
(6, 'DayattAsia', 'GE');

-- --------------------------------------------------------

--
-- Table structure for table `penumpang`
--

CREATE TABLE `penumpang` (
  `id` int(11) NOT NULL,
  `nama_penumpang` varchar(255) NOT NULL,
  `umur` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `penumpang`
--

INSERT INTO `penumpang` (`id`, `nama_penumpang`, `umur`) VALUES
(1, 'Hidayat', 20),
(2, 'Gilang', 23),
(3, 'Roman', 20),
(4, 'Afriza', 20),
(5, 'Saidun', 20),
(7, 'H. Isam', 45),
(8, 'Maman', 21);

-- --------------------------------------------------------

--
-- Table structure for table `tiket`
--

CREATE TABLE `tiket` (
  `id` int(11) NOT NULL,
  `id_maskapai` int(11) DEFAULT NULL,
  `id_penumpang` int(11) DEFAULT NULL,
  `tanggal_pembelian` date DEFAULT NULL,
  `harga` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `tiket`
--

INSERT INTO `tiket` (`id`, `id_maskapai`, `id_penumpang`, `tanggal_pembelian`, `harga`) VALUES
(1, 1, 1, '2025-01-09', 100000.00),
(2, 2, 2, '2025-01-09', 1750000.00),
(3, 3, 5, '2025-01-02', 750000.00),
(4, 2, 3, '2025-01-09', 6700000.00),
(5, 1, 7, '2025-01-02', 1500000.00),
(6, 6, 8, '2025-01-01', 10000000.00);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `maskapai`
--
ALTER TABLE `maskapai`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `penumpang`
--
ALTER TABLE `penumpang`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tiket`
--
ALTER TABLE `tiket`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_maskapai` (`id_maskapai`),
  ADD KEY `id_penumpang` (`id_penumpang`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `maskapai`
--
ALTER TABLE `maskapai`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `penumpang`
--
ALTER TABLE `penumpang`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `tiket`
--
ALTER TABLE `tiket`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `tiket`
--
ALTER TABLE `tiket`
  ADD CONSTRAINT `tiket_ibfk_1` FOREIGN KEY (`id_maskapai`) REFERENCES `maskapai` (`id`),
  ADD CONSTRAINT `tiket_ibfk_2` FOREIGN KEY (`id_penumpang`) REFERENCES `penumpang` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
