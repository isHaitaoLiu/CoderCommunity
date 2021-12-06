/**
 * Created by codedrinker on 2019/6/1.
 */

/**
 * 提交回复
 */
function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    comment2target(questionId, 1, content);
}

function comment2target(targetId, type, content) {
    if (!content) {
        alert("不能回复空内容~~~");
        return;
    }

    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: 'application/json',
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code === 200) {
                window.location.reload();
            } else {
                if (response.code === 2003) {
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        window.open("https://github.com/login/oauth/authorize?client_id=2859958f9f059979ed3a&redirect_uri=" + document.location.origin + "/callback&scope=user&state=1");
                        window.localStorage.setItem("closable", true);
                    }
                } else {
                    alert(response.message);
                }
            }
        },
        dataType: "json"
    });
}

function comment(e) {
    var commentId = e.getAttribute("data-id");
    var content = $("#input-" + commentId).val();
    comment2target(commentId, 2, content);
}

/**
 * 展开二级评论
 */
function collapseComments(e) {
    var id = e.getAttribute("data-id");
    var comments = $("#comment-" + id);

    // 获取一下二级评论的展开状态
    var collapse = e.getAttribute("data-collapse");
    if (collapse) {
        // 折叠二级评论
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else {
        var subCommentContainer = $("#comment-" + id);
        if (subCommentContainer.children().length !== 1) {
            //展开二级评论
            comments.addClass("in");
            // 标记二级评论展开状态
            e.setAttribute("data-collapse", "in");
            e.classList.add("active");
        } else {
            $.getJSON("/comment/" + id, function (data) {
                $.each(data.data.reverse(), function (index, comment) {
                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    }).append($("<img/>", {
                        "class": "media-object img-rounded",
                        "src": comment.user.avatarUrl
                    }));

                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body"
                    }).append($("<h5/>", {
                        "class": "media-heading",
                        "html": comment.user.name
                    })).append($("<div/>", {
                        "html": comment.content
                    })).append($("<div/>", {
                        "class": "menu"
                    }).append($("<span/>", {
                        "class": "pull-right",
                        "html": moment(comment.gmtCreate).format('YYYY-MM-DD')
                    })));

                    var mediaElement = $("<div/>", {
                        "class": "media"
                    }).append(mediaLeftElement).append(mediaBodyElement);

                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                    }).append(mediaElement);

                    subCommentContainer.prepend(commentElement);
                });
                //展开二级评论
                comments.addClass("in");
                // 标记二级评论展开状态
                e.setAttribute("data-collapse", "in");
                e.classList.add("active");
            });
        }
    }
}

/**
 * 问题点赞处理
 */
function like(e) {
    var questionId = e.getAttribute('question-id');
    var status = e.getAttribute('status');

    $.ajax({
        type: "POST",
        url: "/like/question",
        contentType: 'application/json',
        data: JSON.stringify({
            "status": status,
            "questionId": questionId,
        }),
        success: function (response) {
            let thumbsUpFlag = $("#question-thumbs-up"); //获取点赞图标
            let likeCountSpan = $("#like-count-span");

            if (response.code === 200) {
                if (response.data === true){  //点赞或取消点赞操作成功
                    if (status !== "0"){   //消赞
                        status = "0";
                        e.setAttribute("status", "0");
                        thumbsUpFlag.css(
                            {
                                color: "black"
                            }
                        )
                        let count = parseInt(likeCountSpan.text());
                        count--;
                        likeCountSpan.text(count);
                        alert("消赞成功！");
                    } else{
                        status = "1";
                        e.setAttribute("status", "1");
                        thumbsUpFlag.css(
                            {
                                color: "blue"
                            }
                        )
                        let count = parseInt(likeCountSpan.text());
                        count++;
                        likeCountSpan.text(count);
                        alert("点赞成功！");
                    }
                }
            } else {
                alert("服务器异常！")
            }
        },
        dataType: "json"
    });

}

/**
 * 评论点赞处理
 */
function commentLike(e) {
    let commentId = e.getAttribute('comment-id');
    let status = e.getAttribute('status');

    $.ajax({
        type: "POST",
        url: "/like/comment",
        contentType: 'application/json',
        data: JSON.stringify({
            "status": status,
            "commentId": commentId,
        }),
        success: function (response) {
            let str = "#comment-thumbs-up-" + commentId;
            console.log(str);
            let thumbsUpFlag = $(str); //获取评论的点赞图标

            if (response.code === 200) {
                if (response.data === true){  //点赞或取消点赞操作成功
                    if (status !== "0"){   //状态由点赞变为未点赞
                        e.setAttribute("status", "0");
                        thumbsUpFlag.css(
                            {
                                color: "black"
                            }
                        )
                        alert("消赞成功！");
                    } else{
                        e.setAttribute("status", "1");
                        thumbsUpFlag.css(
                            {
                                color: "blue"
                            }
                        )
                        alert("点赞成功！");
                    }
                }
            } else {
                alert("服务器异常！")
            }
        },
        dataType: "json"
    });

}