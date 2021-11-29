create table `like`(
    `id` int auto_increment,
    `user_id` int comment '被点赞的用户id',
    `question_id` int comment '点赞的文章id',
    `status` tinyint(1) default '1' comment '点赞状态，0取消，1点赞',
    gmt_create bigint not null,
    gmt_modified bigint not null,
    primary key(`id`),
    INDEX `liked_user_id`(`user_id`),
    INDEX `liked_post_id`(`question_id`)
) comment '用户点赞表';