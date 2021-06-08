alter table comment modify id int auto_increment;
alter table comment modify parent_id int not null comment '被评论的条目id';
alter table comment modify like_count int default 0 not null;