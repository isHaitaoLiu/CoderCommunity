# 1. CoderCommunity
- [码问社区](https://github.com/codedrinker/community) 项目的翻版。是一个开源论坛，现有功能提问、回复和二级回复、通知、最热问题排行等功能。
- 技术栈：Spring boot、Mybatis、Mysql、BootStrap、Ajax、Thymeleaf
# 2. 相关工具及介绍
1. OAuth
    - OAuth 是一个开放标准，该标准允许用户让第三方应用访问该用户在某一网站上存储的私密资源（如头像、照片、视频等），而在这个过程中无需将用户名和密码提供给第三方应用。实现这一功能是通过提供一个令牌（token），而不是用户名和密码来访问他们存放在特定服务提供者的数据。采用令牌（token）的方式可以让用户灵活的对第三方应用授权或者收回权限。OAuth2 是 OAuth 协议的下一版本。
    - OAuth2授权码模式步骤
        1. 用户访问客户端，后者将前者导向认证服务器。
        2. 用户选择是否给予客户端授权。
        3. 假设用户给予授权，认证服务器将用户导向客户端事先指定的"重定向URI"（redirection URI），同时附上一个授权码。
        4. 客户端收到授权码，附上早先的"重定向URI"，向认证服务器申请令牌。这一步是在客户端的后台的服务器上完成的，对用户不可见。
        5. 认证服务器核对了授权码和重定向URI，确认无误后，向客户端发送访问令牌（access token）和更新令牌（refresh token）。
    - 步骤1参数
        1. response_type：表示授权类型，必选项，此处的值固定为"code"
        2. client_id：表示客户端的ID，必选项
        3. redirect_uri：表示重定向URI，可选项
        4. scope：表示申请的权限范围，可选项
        5. state：表示客户端的当前状态，可以指定任意值，认证服务器会原封不动地返回这个值。
    - 步骤3参数
        1. code：表示授权码，必选项。该码的有效期应该很短，通常设为10分钟，客户端只能使用该码一次，否则会被授权服务器拒绝。该码与客户端ID和重定向URI，是一一对应关系。
        2. state：如果客户端的请求中包含这个参数，认证服务器的回应也必须一模一样包含这个参数。
    - 步骤4参数
        1. grant_type：表示使用的授权模式，必选项，此处的值固定为"authorization_code"。
        2. code：表示上一步获得的授权码，必选项。
        3. redirect_uri：表示重定向URI，必选项，且必须与A步骤中的该参数值保持一致。
        4. client_id：表示客户端ID，必选项。
    - 步骤5参数
        1. access_token：表示访问令牌，必选项。
        2. token_type：表示令牌类型，该值大小写不敏感，必选项，可以是bearer类型或mac类型。
        3. expires_in：表示过期时间，单位为秒。如果省略该参数，必须其他方式设置过期时间。
        4. refresh_token：表示更新令牌，用来获取下一次的访问令牌，可选项。
        5. scope：表示权限范围，如果与客户端申请的范围一致，此项可省略。
    - [创建 OAuth 应用程序步骤文档](https://docs.github.com/en/developers/apps/building-oauth-apps/creating-an-oauth-app)
    - [授权 OAuth 应用程序步骤文档](https://docs.github.com/en/developers/apps/building-oauth-apps/authorizing-oauth-apps)
# 3. 项目开发记录文档
## 3.0 数据库设计
- 用户表：存储从github获取的用户信息，包括唯一id、用户名、access_token、头像、创建和修改时间等
- 问题表：存储问题标题、详细描述、标签、创建时间、修改时间、问题所属者id、评论数、点赞数、浏览数等
- 评论表：存储评论人id、评论的条目类别、评论的条目id、评论的问题id、评论内容、评论数、点赞数等
- 通知表：存储通知发起者、通知接收者、通知类别、通知详情、通知状态等
## 3.1 第三方(github)登录开发
- 应用程序在github中注册
    - 参考[创建 OAuth 应用程序步骤文档](https://docs.github.com/en/developers/apps/building-oauth-apps/creating-an-oauth-app)
    - 在注册过程中需要设置github进行回调的地址**redirect_uri**
    - 在注册结束后获得**Client ID**和**Client secrets**
- 授权登录流程(参照2.1中的OAuth2授权码模式步骤进行)
    1. 用户在前端页面触发登录事件，将地址定向到 https://github.com/login/oauth/authorize 请求用户的github授权
        - 参数有：client_id、redirect_uri、scope、state
        - 示例
        ```html
        <!-- 这里的地址是固定的 -->
        <a href="https://github.com/login/oauth/authorize
        ?client_id=xxxxxxxxx
        &redirect_uri=http://localhost:8080/callback
        &scope=user
        &state=1">
        登录
        </a>
        ```
    2. 用户同意授权，github认证服务器将用户重定向回**redirect_uri**，会传回一个code和刚发送的state
    3. 后端收到code, 向github发送post请求，来获取访问令牌access_token
        - 地址：https://github.com/login/oauth/access_token
        - 参数有：client_id、client_secret、code
    4. github对client_id、client_secret、code进行验证，通过后将返回一个临时访问令牌access_token
    5. 应用程序后端可以拿着access_token向github申请用户信息
        - 地址：https://api.github.com/user
        - 请求头信息：Authorization: token access_token
- 程序逻辑
    - 【前端】在任一页面中，设置登录按钮，绑定登录事件，登录事件定向到 https://github.com/login/oauth/authorize ，核心参数需要完整
    - 【后端】
        - 添加一个处理 redirect_uri 路径的Controller，接收code和state参数
        - 构造一个post请求，填写接收的code，在请求结果中拿到access_token
        - 构造get请求，获取用户信息，写入cookie并存入数据库
## 3.2 提问功能和问题修改功能
- 提问功能程序逻辑
    - 前端
        - 在index页面，只有登录的用户才有提问按钮，点击提问按钮进入publish页面
        - publish页面，由标题、问题描述、标签构成一个form表单，发布按钮进行问题提交
    - 后端
        - 标题、问题描述、标签任何一个没填，则提示相应错误，返回publish页面
        - 如果用户未登录，提示错误，返回publish页面
        - 只有登录用户进行完整填写才可成功发布问题，将问题写入数据库，跳回主页
- 问题修改程序逻辑
    - 前端
        - 问题发布后，提供编辑按钮，以便用户进行修改
        - 点击编辑按钮，将进入编辑功能的后端部分
    - 后端
        - 验证问题发起者是否是当前用户，如果不是则返回问题页面，如果是则进入publish页面
        - 之后进入提问功能程序逻辑
## 3.3 评论和二级评论功能
- 评论功能程序逻辑
    - 前端
        - question页面，即问题详情页面展示问题的标题、详细描述、标签、问题点赞、评论、浏览数
        - 在这之后，有历史评论列表
        - 最后，有一个评论文本框，在文本框内进行评论编写，点击回复进行提交，提交一个Ajax请求
    - 后端
        - 接收评论的问题id、评论的条目id、评论的类别、评论内容信息
        - 验证用户登录信息、验证评论是否合法，不合法将返回json格式错误码
        - 将此条评论写入数据库，返回json格式侧成功码
- 二级评论功能程序逻辑
    - 前端
        - 在每个评论下方显示一个评论icon，点击将列出该评论的所有二级评论
        - 在最底部有一个评论输入框和一个提交按钮
        - 点击按钮，提交一个Ajax请求
    - 后端
        - 接收评论的问题id、评论的条目id、评论的类别、评论内容信息
        - 验证用户登录信息、验证评论是否合法，不合法将返回json格式错误码
        - 将此条评论写入数据库，返回json格式成功码
## 3.4 通知功能
## 3.5 热门话题功能
## 3.6 其他
- 登录拦截器
- 全局异常处理器