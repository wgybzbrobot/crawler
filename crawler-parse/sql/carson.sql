-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- 主机: localhost
-- 生成日期: 2014-05-20 10:14:43
-- 服务器版本: 5.5.37-0ubuntu0.14.04.1
-- PHP 版本: 5.5.9-1ubuntu4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- 数据库: `carson`
--

-- --------------------------------------------------------

--
-- 表的结构 `categroy`
--

CREATE TABLE IF NOT EXISTS `categroy` (
  `name` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `code` varchar(45) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='爬取类型';

--
-- 转存表中的数据 `categroy`
--

INSERT INTO `categroy` (`name`, `code`) VALUES
('论坛', 'forum'),
('新闻资讯', 'news'),
('百度贴吧', 'tieba');

-- --------------------------------------------------------

--
-- 表的结构 `conf_list`
--

CREATE TABLE IF NOT EXISTS `conf_list` (
  `comment` varchar(50) NOT NULL,
  `url` varchar(150) NOT NULL COMMENT '种子',
  `category` varchar(45) NOT NULL COMMENT '类型: forum, news',
  `ajax` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是ajax加载页面0false,1true',
  `numThreads` int(11) NOT NULL DEFAULT '1' COMMENT '解析线程数',
  `fetchinterval` int(11) NOT NULL COMMENT '更新时间间隔，单位是minute',
  `pageNum` tinyint(4) NOT NULL DEFAULT '1' COMMENT '翻页数',
  `fetchurl` varchar(300) NOT NULL COMMENT '抓取的url正则表达式',
  `filterurl` varchar(300) DEFAULT NULL COMMENT '过滤url正则表达式，可设置图片过滤',
  `listdom` varchar(300) NOT NULL COMMENT '列表区域的DOM结构',
  `linedom` varchar(100) NOT NULL COMMENT '一行记录信息的DOM结构',
  `urldom` varchar(100) NOT NULL,
  `datedom` varchar(100) NOT NULL COMMENT '发布时间DOM',
  `page` varchar(100) DEFAULT NULL COMMENT '列表页分页，若isAjax则上一页，下一页；若不是则是下一页的DOM',
  PRIMARY KEY (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='“列表页”配置，包含url种子';

--
-- 转存表中的数据 `conf_list`
--

INSERT INTO `conf_list` (`comment`, `url`, `category`, `ajax`, `numThreads`, `fetchinterval`, `pageNum`, `fetchurl`, `filterurl`, `listdom`, `linedom`, `urldom`, `datedom`, `page`) VALUES
('百度贴吧-蚌埠吧', 'http://tieba.baidu.com/f?kw=%B0%F6%B2%BA', 'tieba', 0, 10, 5, 2, 'http://tieba.baidu.com/p/.*', '', 'ul#thread_list', 'li.j_thread_list', 'a.j_th_tit', 'span.threadlist_reply_date', 'div#frs_list_pager a.next');

-- --------------------------------------------------------

--
-- 表的结构 `forum`
--

CREATE TABLE IF NOT EXISTS `forum` (
  `url` varchar(200) NOT NULL COMMENT '主帖url',
  `title` varchar(120) DEFAULT NULL COMMENT '主帖标题',
  `releasedate` datetime DEFAULT NULL COMMENT '发布时间',
  `fetchdate` datetime DEFAULT NULL,
  `content` mediumtext COMMENT '帖子内容',
  `author` varchar(45) DEFAULT NULL,
  `replyNum` int(11) DEFAULT '-1',
  `reviewNum` int(11) DEFAULT '-1',
  `imgUrl` text,
  `audioUrl` text,
  `videoUrl` text COMMENT '论坛',
  PRIMARY KEY (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `forumconf_detail`
--

CREATE TABLE IF NOT EXISTS `forumconf_detail` (
  `comment` varchar(45) DEFAULT NULL COMMENT '注释',
  `host` varchar(150) NOT NULL COMMENT '页和抓取内容页面的正则表达式 组合主键，如 http://tieba.baidu.com 和 http://tieba.baidu.com/p/.* ;http://tieba.baidu.com 和 http://tieba.baidu.com/photo/.*',
  `replyNum` varchar(100) DEFAULT NULL COMMENT '-1 表示未获取到',
  `reviewNum` varchar(100) DEFAULT NULL,
  `forwardNum` varchar(100) DEFAULT NULL,
  `pagebar` varchar(100) DEFAULT NULL COMMENT '翻页条',
  `fetchorder` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'true从最后一页抓',
  `master` varchar(100) DEFAULT NULL,
  `masterAuthor` varchar(100) DEFAULT NULL,
  `masterDate` varchar(100) DEFAULT NULL,
  `masterContent` varchar(100) DEFAULT NULL,
  `reply` varchar(100) DEFAULT NULL COMMENT '回复DOM',
  `replyAuthor` varchar(100) DEFAULT NULL,
  `replyDate` varchar(100) DEFAULT NULL,
  `replyContent` varchar(100) DEFAULT NULL,
  `subReply` varchar(100) DEFAULT NULL COMMENT '子回复DOM，暂时不考虑子回复分页',
  `subReplyAuthor` varchar(100) DEFAULT NULL,
  `subReplyDate` varchar(100) DEFAULT NULL,
  `subReplyContent` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`host`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `forumconf_detail`
--

INSERT INTO `forumconf_detail` (`comment`, `host`, `replyNum`, `reviewNum`, `forwardNum`, `pagebar`, `fetchorder`, `master`, `masterAuthor`, `masterDate`, `masterContent`, `reply`, `replyAuthor`, `replyDate`, `replyContent`, `subReply`, `subReplyAuthor`, `subReplyDate`, `subReplyContent`) VALUES
('中安论坛', 'http://bbs.anhuinews.com', 'div#postlist table tbody tr td div span:eq(4)', 'div#postlist div.hm.ptn span:eq(1)', '', 'div#pgt', 1, 'div#postlist>div:contains(分享到)', 'td.pls div div.pi div.authi a', 'div.pti div.authi em', 'div.pct div.pcb div.t_fsz', 'div#postlist>div:contains(反对)', 'tr td.pls div div.pi div.authi a', 'div.authi  em', 'div.pct div.pcb', NULL, NULL, NULL, NULL),
('淮北人', 'http://bbs.hb163.cn/forum.php', 'div#postlist div.hm span:eq(1) em', 'div#postlist div.hm span:eq(0) em', '', NULL, 1, 'div#postlist>div:contains(分享到)', 'td.pls div div.pi div.authi a', 'div.pti div.authi em', 'div.pct div.pcb div.t_fsz', 'div#postlist>div:contains(反对)', 'tr td.pls div div.pi div.authi a', 'div.authi  em', 'div.pct div.pcb', NULL, NULL, NULL, NULL),
('合肥论坛', 'http://bbs.hefei.cc', 'p.hm span:eq(1) em:eq(1)', 'p.hm span:eq(1) em:eq(0)', NULL, 'div#pgt div.pgt', 1, 'div#postlist div>table>tbody>tr:eq(0)', 'div.authi a:eq(0)', NULL, 'td.t_f', 'div#postlist>div[id]>table[id]>tbody>tr:eq(0)', 'div.authi a:eq(0)', 'div.authi em', 'div.pcb', NULL, NULL, NULL, NULL),
('大闽社区论坛', 'http://myfj.qq.com', 'div.authi span:eq(2)', 'div.authi span:eq(1) strong', NULL, 'div.pgt.page_box div.pgb', 1, 'div#postlist div[id]:eq(0)', 'div.author_box div.cl:eq(1) a', 'div.authi span.tm em', 'div.t_fsz', 'div#postlist div[id] table.plhin', 'div.author_box div.cl:eq(1) a', 'div.p_t.cl span em', 'div.t_fsz', NULL, NULL, NULL, NULL),
('百度贴吧', 'http://tieba.baidu.com', 'ul.l_posts_num li.l_reply_num span:eq(0)', NULL, '', 'div.l_thread_info ul.l_posts_num', 1, 'div.l_post.noborder', 'ul.p_author li.d_name a.p_author_name', 'div.core_reply_tail  ul.p_tail  li:eq(1)  span', 'div.d_post_content', 'div.l_post.l_post_bright', 'a.p_author_name', 'div.core_reply_tail  ul.p_tail  li:eq(1)  span', 'div.d_post_content', 'li.lzl_single_post  div.lzl_cnt', 'div.lzl_cnt a.j_user_card', 'div.lzl_content_reply   span.lzl_time', 'span.lzl_content_main'),
('猫扑论坛', 'http://tt.mop.com', 'div.num span.fcR:eq(1)', 'div.num span.fcR:eq(0)', NULL, 'div.page div.inner', 1, 'div.tzbdP', 'div.name a.fcB', 'span.date', 'div#js-sub-body', 'div.js-reply', 'div.fl a.h_yh', 'li.htc2 div.h_lz', 'div.h_nr.js-reply-body', NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- 表的结构 `news`
--

CREATE TABLE IF NOT EXISTS `news` (
  `url` varchar(200) NOT NULL,
  `title` varchar(500) DEFAULT NULL,
  `content` longtext,
  `sources` varchar(45) DEFAULT NULL,
  `img` mediumtext,
  `audio` mediumtext,
  `video` mediumtext,
  `author` varchar(45) DEFAULT NULL,
  `releaseDate` datetime DEFAULT NULL,
  `fetchDate` datetime DEFAULT NULL,
  `replyNum` int(11) DEFAULT NULL,
  `forwardNum` int(11) DEFAULT NULL,
  `reviewNum` int(11) DEFAULT NULL,
  PRIMARY KEY (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `newsconf_detail`
--

CREATE TABLE IF NOT EXISTS `newsconf_detail` (
  `comment` varchar(45) DEFAULT NULL COMMENT '资讯类DOM结构',
  `host` varchar(100) NOT NULL DEFAULT '',
  `content` varchar(300) DEFAULT NULL,
  `sources` varchar(300) DEFAULT NULL,
  `author` varchar(300) DEFAULT NULL,
  `replyNum` varchar(300) DEFAULT NULL,
  `forwardNum` varchar(300) DEFAULT NULL,
  `reviewNum` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`host`),
  UNIQUE KEY `host` (`host`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='资讯新闻详细页配置';

--
-- 转存表中的数据 `newsconf_detail`
--

INSERT INTO `newsconf_detail` (`comment`, `host`, `content`, `sources`, `author`, `replyNum`, `forwardNum`, `reviewNum`) VALUES
('新浪新闻娱乐', 'http://ent.sina.com.cn', 'div#artibody', 'span#media_name span a:eq(0)', NULL, 'a#media_comment span.f_red', NULL, NULL),
('新浪新闻财经', 'http://finance.sina.com.cn', 'div#artibody', 'span#media_name span a:eq(0)', NULL, 'a#media_comment span.f_red', NULL, NULL),
('新浪新闻军事', 'http://mil.news.sina.com.cn', 'div#artibody', 'span#media_name span a:eq(0)', NULL, 'a#media_comment span.f_red', NULL, NULL),
('腾讯新闻', 'http://news.qq.com', 'div#Cnt-Main-Article-QQ', 'div.color-a-1 a', NULL, 'a#cmtNum', NULL, NULL),
('新浪新闻', 'http://news.sina.com.cn', 'div#artibody', 'span#media_name span a:eq(0)', NULL, 'a#media_comment span.f_red', NULL, NULL),
('新浪新闻滚动', 'http://roll.news.sina.com.cn', 'div#artibody', 'span#media_name span a:eq(0)', NULL, 'a#media_comment span.f_red', NULL, NULL),
('新浪新闻组图', 'http://slide.ent.sina.com.cn', 'div#eData', 'span#media_name span a:eq(0)', NULL, 'a#media_comment span.f_red', NULL, NULL),
('新浪新闻体育', 'http://sports.sina.com.cn', 'div#artibody', 'span#media_name span a:eq(0)', NULL, 'a#media_comment span.f_red', NULL, NULL),
('新浪新闻-新闻', 'http://tech.sina.com.cn', 'div#artibody', 'span#media_name span a:eq(0)', NULL, 'a#media_comment span.f_red', NULL, NULL),
('新浪新闻视频', 'http://video.sina.com.cn', 'div#eData', 'span#media_name span a:eq(0)', NULL, 'a#media_comment span.f_red', NULL, NULL);

-- --------------------------------------------------------

--
-- 表的结构 `reply`
--

CREATE TABLE IF NOT EXISTS `reply` (
  `id` varchar(45) NOT NULL,
  `parentId` varchar(45) DEFAULT NULL,
  `mainUrl` varchar(200) DEFAULT NULL,
  `authorAccount` varchar(100) DEFAULT NULL,
  `content` mediumtext,
  `videoUrl` text,
  `imgUrl` text,
  `audioUrl` text,
  `title` varchar(100) DEFAULT NULL,
  `releasedate` datetime DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL COMMENT '用户归属地',
  `md5` varchar(120) DEFAULT NULL COMMENT ' MD5 unique id: calculate by mainUrl, author, content, imgurl, audiourl, videourl',
  `currentUrl` varchar(200) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `seed`
--

CREATE TABLE IF NOT EXISTS `seed` (
  `url` varchar(150) COLLATE utf8_unicode_ci NOT NULL COMMENT '种子',
  `indexUrl` varchar(300) COLLATE utf8_unicode_ci NOT NULL COMMENT '种子首页',
  `fetchinterval` int(11) DEFAULT '0' COMMENT '时间间隔',
  `remain` int(11) DEFAULT '0' COMMENT '剩余时间，单位分钟',
  `type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1.列表页、2.详细页首页、3.详细页回复页',
  `lose` tinyint(1) NOT NULL DEFAULT '0',
  `title` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
  `releasedate` datetime DEFAULT NULL,
  `mainUrl` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DELIMITER $$
--
-- 事件
--
CREATE DEFINER=`root`@`localhost` EVENT `reduce_remain` ON SCHEDULE EVERY 1 MINUTE STARTS '2014-01-05 09:38:09' ON COMPLETION PRESERVE ENABLE COMMENT '每隔1分钟减少seed的remain时间' DO update seed SET remain = remain - 1 where remain > 0$$

DELIMITER ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
