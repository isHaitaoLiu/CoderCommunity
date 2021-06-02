# 1.CoderCommunity

# 2.开发文档
## 2.1 第三方(github)登录开发
1. [创建 OAuth 应用程序](https://docs.github.com/en/developers/apps/building-oauth-apps/creating-an-oauth-app)
2. [授权 OAuth 应用程序](https://docs.github.com/en/developers/apps/building-oauth-apps/authorizing-oauth-apps)
    - 流程
        1. 用户在web程序页面触发登录事件
        2. 事件触发，定向到github请求用户的github身份
            - get请求
            - 地址：https://github.com/login/oauth/authorize，参数如下
            - client_id ：填写【创建 OAuth 应用程序】步骤中创建的id
            - redirect_uri：填写【创建 OAuth 应用程序】步骤中填写的回调地址
            - scope ：web程序获取的OAuth 令牌对那个github账户的权限，这里可以使用user，授予对个人资料信息的读/写访问权限
            - state ：一个不可猜测的随机字符串
        3. 用户被重定向回web站点（【创建 OAuth 应用程序】步骤中填写的回调地址），会传回一个code和刚发送的state
        4. web站点向github发送post请求，来获取获取用户github账户信息的access_token
            - post请求
            - 地址：https://github.com/login/oauth/access_token
            - 参数client_id：填写【创建 OAuth 应用程序】步骤中创建的id
            - 参数client_secret：填写【创建 OAuth 应用程序】步骤中创建的secret
            - 参数code：填写github刚返回的code
        5. 接收github响应，获取access_token
        6. web站点拿着access_token，申请用户信息
            - get请求
            - 地址：https://api.github.com/user
            - 请求头信息：Authorization: token access_token
    - 程序逻辑
        - 【前端逻辑】在html网页中，登录事件即定向到github，发起get请求
        - 【自动行为】由于在请求中有redirect_url参数，用户输入github账号后，会重定向回这个地址
        - 【后端行为】
            - 后端处理重定向的请求，在这个请求中接收code和state参数
            - 构造一个post请求，填写接收的code和已有的参数
            - 拿到access_token
            - 构造get请求，获取用户信息
## 2.2 发布页面开发
   - 程序逻辑
        - 【前端布局】publish页面，由标题、问题描述、标签构成一个form表单，发布按钮进行提交
        - 【数据库】添加问题表，存储问题相关信息、问题发起人，时间、点赞数、查看数、评论数
        - 【后端逻辑】
            - index主页，只有登录用户才显示发布按钮
            - publish页面，如果标题、问题描述、标签任何一个没填，都会提示相应错误，返回publish页面；如果用户未登录
            也会提示错误，返回publish页面；只有登录用户进行完整填写才可成功发布，并跳回主页
