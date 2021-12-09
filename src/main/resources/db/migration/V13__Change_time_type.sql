alter  table `comment` modify  column gmt_create datetime NOT NULL COMMENT '创建时间';
alter  table `comment` modify  column gmt_modified datetime NOT NULL COMMENT '修改时间';

alter  table `comment_like` modify  column gmt_create datetime NOT NULL COMMENT '创建时间';
alter  table `comment_like` modify  column gmt_modified datetime NOT NULL COMMENT '创建时间';

alter  table `questionLike` modify  column gmt_create datetime NOT NULL COMMENT '创建时间';
alter  table `questionLike` modify  column gmt_modified datetime NOT NULL COMMENT '修改时间';

alter  table `notification` modify  column gmt_create datetime NOT NULL COMMENT '创建时间';

alter  table `question` modify  column gmt_create datetime NOT NULL COMMENT '创建时间';
alter  table `question` modify  column gmt_modified datetime NOT NULL COMMENT '修改时间';

alter  table `user` modify  column gmt_create datetime NOT NULL COMMENT '创建时间';
alter  table `user` modify  column gmt_modified datetime NOT NULL COMMENT '修改时间';