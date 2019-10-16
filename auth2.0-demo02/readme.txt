这里使用postman(接口测试工具)去对接口做一些简单的测试。

(1)这里我去发送一个获取用户列表的请求:
GET 127.0.0.1:7777/auth/user
 结果可以看到，由于没有携带token信息，所以返回了如下信息。
{
    "code": 1004,
    "message": "访问此资源需要完全的身份验证"
}

(2)接下来，我们先去登录。
POST 127.0.0.1:7777/auth/user/login?account=admin&password=123456

返回：
{
    "code": 0,
    "data": {
        "accessToken": "a8e664bc-1352-4cf8-9948-d513551f4186",
        "accessTokenExpiration": "2019-10-12 17:42:43",
        "accessTokenExpiresIn": 29,
        "account": "admin",
        "expired": false,
        "id": 1,
        "name": "小小丰",
        "password": "123456",
        "refreshToken": "3e52d491-ee6e-4c0b-b47a-92477961a323",
        "refreshTokenExpiration": "2019-10-12 18:12:13",
        "scope": [
            "all"
        ],
        "tokenType": "bearer"
    },
    "message": "成功"
}

code模式
{
	"expiration":"2019-10-16 15:34:01",
	"expired":false,
	"expiresIn":1199,
	"scope":[
		"all"
	],
	"tokenType":"bearer",
	"value":"6bdac299-5572-463c-a33f-bde5b34e04e4"
}
登录成功后，这里会返回一系列信息，记住这个token信息，待会我们尝试使用这个token信息再次请求上面那个获取用户列表接口。

(3)携带token去获取用户列表
GET 127.0.0.1:7777/auth/user
在POST Man上选择Authorization选择OAuth2.0，填写登陆接口返回的token
可以看到，可以成功拿到接口返回的资源(用户的列表信息)啦。
{
    "code": 0,
    "data": [
        {
            "account": "admin",
            "description": "系统默认管理员",
            "id": 1,
            "name": "小小丰",
            "password": "123456",
            "role": {
                "createdTime": 1570869479,
                "description": "管理员拥有所有接口操作权限",
                "id": 1,
                "name": "管理员",
                "role": "ADMIN"
            }
        },
        {
            "account": "xiaowang",
            "createdTime": 1570870297433,
            "description": "添加一个新用户",
            "id": 2,
            "name": "小王",
            "password": "123456",
            "role": {
                "createdTime": 1570869479,
                "description": "普通拥有查看用户列表与修改密码权限，不具备对用户增删改权限",
                "id": 2,
                "name": "普通用户",
                "role": "USER"
            }
        }
    ],
    "message": "成功"
}

(4)这里测试一下，用户注销的接口。用户注销，会把redis里的token信息全部清除。
GET 127.0.0.1:7777/auth/user/logout
在POST Man上选择Authorization选择OAuth2.0，填写登陆接口返回的token
可以看到，注销成功了。那么我们再用这个已经被注销的token再去请求一遍那个获取用户列表接口。
退出登录，清除token后，再访问获取用户列表接口，返回
{
    "code": 1001,
    "message": "access_token无效"
}

很显然，此时已经报token无效了。

　接下来，我们对角色的资源分配管理进行一个测试。可以看到我们库里面，
项目初始化的时候，就已经创建了一个管理员，我们上面配置已经规定，
管理员是拥有所有接口的访问权限的，而普通用户却只有查询权限。我们现在就来测试一下这个效果。


（1）首先我使用该管理员去添加一个普通用户。
POST 127.0.0.1:7777/auth/user 
在POST Man上选择Authorization选择OAuth2.0，填写登陆接口返回的token
BODY
{
	"account":"xiaohong",
	"description":"再添加一个新用户",
	"name":"小红",
	"password":"123456",
	"roleId":1
}

可以看到，我们返回了添加成功信息了，那么我去查看一下用户列表。
很显然，现在这个用户已经成功添加进去了。

(2)接下来，我们用新添加的用户去登录一下该系统。
该用户也登录成功了，我们先保存这个token。
(3)我们现在携带着刚才登录的普通用户"小红"的token去添加一个普通用户。
可以看到，由于"小红"是普通用户，所以是不具备添加用户的权限的。
{
    "code": 1003,
    "message": "该用户权限不足以访问该资源接口"
}


(4)那么我们现在用"小王"这个用户去查询一下用户列表。
可以看到，"小王"这个普通用户是拥有查询用户列表接口的权限的。

---------------------延伸----------------------
以下接口为框架自带的，不用开发

(1)有时候我们防止token泄露，token有效期很短，比如30秒，此时可以把refresh_token
有效期设置长一点

如果一旦发现token泄露或者失效，可以通过接口刷新token，此时新生成的token生效，原来的一律失效

POST 127.0.0.1:7777/oauth/token?grant_type=refresh_token&refresh_token=3e52d491-ee6e-4c0b-b47a-92477961a323&client_id=client_1&client_secret=123456
返回：
{
    "additionalInformation": {},
    "expiration": "2019-10-12 17:46:04",
    "expired": false,
    "expiresIn": 29,
    "refreshToken": {
        "expiration": "2019-10-12 18:15:34",
        "value": "b6da83d3-6112-443c-ab25-4b124b0e0143"
    },
    "scope": [
        "all"
    ],
    "tokenType": "bearer",
    "value": "030505dd-e293-4c76-8c44-46a2caa3fd1e"
}

(2) 检测token是否有效

GET 127.0.0.1:7777/oauth/check_token?token=da5947aa-54ff-421a-91db-553855c0d1e1

{
    "active": true,
    "exp": 1570874208,
    "user_name": "admin",
    "authorities": [
        "ADMIN"
    ],
    "client_id": "client_1",
    "scope": [
        "all"
    ]
}
