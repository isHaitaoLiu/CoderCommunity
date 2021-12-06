create table `comment_like`(
                       `id` int auto_increment,
                       `user_id` int comment '被点赞的用户id',
                       `comment_id` int comment '点赞的评论id',
                       `status` tinyint(1) default '0' comment '点赞状态，0取消，1点赞',
                       gmt_create bigint not null,
                       gmt_modified bigint not null,
                       primary key(`id`),
                       unique key(`user_id`, `comment_id`),
                       INDEX `liked_user_id`(`user_id`),
                       INDEX `liked_comment_id`(`comment_id`)
) comment '评论点赞表';