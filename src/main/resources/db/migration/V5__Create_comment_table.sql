create table comment(
    id bigint primary key auto_increment,
    parent_id bigint not null comment '被评论的条目id',
    type int not null comment '被评论的条目类型',
    commentator int not null comment '此条评论持有者',
    gmt_create bigint not null,
    gmt_modified bigint not null,
    like_count bigint not null default 0
)