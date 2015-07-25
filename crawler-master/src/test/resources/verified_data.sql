-- phpMyAdmin SQL Dump
-- version 4.2.2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: 2015-07-25 07:26:00
-- 服务器版本： 5.5.37-MariaDB-log
-- PHP Version: 5.5.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `crawler`
--

-- --------------------------------------------------------

--
-- 表的结构 `verified_data`
--

CREATE TABLE IF NOT EXISTS `verified_data` (
`id` int(11) NOT NULL,
  `filename` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `keyword` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `record` longtext COLLATE utf8mb4_unicode_ci,
  `lasttime` datetime NOT NULL COMMENT '抓取时间'
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=6 ;

--
-- 转存表中的数据 `verified_data`
--

INSERT INTO `verified_data` (`id`, `filename`, `keyword`, `record`, `lasttime`) VALUES
(1, 'test0', '广场', '{"id":"0C64F84C8B7335A25365054F8B039B49","platform":1.0,"url":"http://news.xinhuanet.com/house/nj/2015-04-09/c_1114916566.htm","title":"边城V时代广场牵手戴德梁行 开启战略合作新篇章","type":"网易新闻搜索","isharmful":false,"content":"&middot;北京庙会“空降”台北　市民吃玩乐翻天(22:17)\\n&middot;土美将联合训练装备叙利亚反对派武装(22:16)\\n&middot;少林功夫亮相斐济(22:16)\\n&middot;印度尼西亚英德岛东北海域发生６.９级深源地震(22:04)\\n&middot;塞拉利昂发生埃博拉血样“乌龙”事件(21:55)\\n&middot;俄罗斯成功发射一颗军用卫星(21:55)\\n&middot;（授权发布）全国人民代表大会常务委员会任命名单(21:54)\\n&middot;（授权发布）全国人民代表大会常务委员会任免名单(21:54)\\n&middot;香港降按揭成数为楼市降温(21:23)\\n&middot;梁振英：特区政府必将用好财政收入(21:23)\\n&middot;外交部发言人驳美国家情报总监关于南海言论(21:22)\\n&middot;李克强会见斯里兰卡外长(21:19)\\n&middot;全国人大常委会授权国务院在三十三个试点县（市、区）暂时调整实施土地管理法的相关规定(21:19)\\n&middot;“新闻+创意”：新华社发布客户端下载量超３１００万(21:10)\\n&middot;德国议会批准延长希腊救助协议(20:52)\\n&middot;击楫中流　迎难勇进——两会时间看全面深化改革“关键棋”(20:43)\\n&middot;十二届全国人大常委会第四十三次委员长会议在京举行(20:41)\\n&middot;中国土地确权登记颁证试点新增九省区(20:38)\\n&middot;陈吉宁履新：给中国环保带来“新风”　(20:38)\\n前言：“强强联手，共启未来”，4月10日，边城V时代广场将与戴德梁行等超过10位商家代表签订战略合作协议，多个实力商家对区域商业的未来发展及前景表示认可，认为新鼓楼滨江区域商业必将大有可为。届时边城V公馆40-70㎡小公寓三大主力户型，也将提前揭开面纱。\\n强强联手，“寓”见新鼓楼未来财富\\n4月10日，边城V时代广场与戴德梁行等商家的签约仪式将在边城V时代广场营销中心隆重举行，双方正式牵手合作，强强联手，将为客户带来更多惊喜。届时，在精心布置的签约仪式现场，边城领导将会为在座的来宾细细解读“时代之光，V领时尚”的潮流理念，并详细介绍边城V时代广场和边城V公馆的未来投资发展趋势。\\n据悉，当天营销中心还为到场的每位客户准备了精美的餐点茶歇，并穿插激动人心的抽奖环节。4月10日，就来边城V时代广场，细品城先生的咖啡香醇，“寓”见新鼓楼的未来财富。\\n优质资源，打造外滩全新“V”时代\\n边城V时代广场位于渡江纪念碑旁，与证大大拇指广场仅一街之隔，在区位上，边城V时代广场可谓位于鼓楼滨江CBD核心区——南京新外滩，民国建筑与长江黄金水岸傲视全城，政府近200万的年投资，一大批房企的联合开发，使新鼓楼滨江区域被誉为最有可能成为南京第二个河西的，黄金发展地块。\\n而边城V时代广场的热度不仅仅在于地段的热门，其项目总建筑面积13万平米，涵盖酒店式公寓、写字楼、ShoppingMall和沿街商铺四种形态，4月首推B地块SOHO公寓，商住两用，面积段40-70平方米。在交通方面，项目与规划中的5、9号线无缝对接，为真正的地铁盘，不用出楼就直达地铁层，从此南京主城，一轨通达！边城V时代广场的面市，或将成为整个鼓楼滨江的力鼎之作。\\n","timestamp":"2015-04-09T15:32:00Z","source_id":936.0,"lasttime":"2015-04-09T23:32:30Z","server_id":6141.0,"keyword":"广场","first_time":"2015-04-09T23:32:30Z","ip":"220.181.76.27","location":"北京市 电信互联网数据中心","source_name":"网易新闻搜索","source_type":2.0,"country_code":1.0,"location_code":110000.0,"_version_":1.49798892230567526E18}', '2015-07-24 17:31:07'),
(4, 'fef', 'feff', 'fgefgg', '2015-07-25 00:00:00');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `verified_data`
--
ALTER TABLE `verified_data`
 ADD PRIMARY KEY (`id`), ADD KEY `filename` (`filename`), ADD KEY `lasttime` (`lasttime`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `verified_data`
--
ALTER TABLE `verified_data`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=6;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
