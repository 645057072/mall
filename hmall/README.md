hmall-learning



2019-6-3
1.引入lombock，可实现对实体类的注解开发。
2.配置项目maven隔离，实现线上线下开发隔离环境
3.引入redis连接池接口，实现redis缓存，实现读写分离、

2019-6-5
1.增加CookieUtil用于单点登录，Cookie重写方法
2.用户单点登录配置cookis，redis获取和删除TOKEN
3.设置Session过期时间为30分钟


2019-6-6
1.使用Redis存储替换GUAVACAHCE,用于TOKEN管理TOKEN信息

2019-6-7
1.创建SHAREDJEDIS API替换JEDIS API实现REDIS分布式管理。

2019-6-15
1.增加全局异常处理，将HTTP请求的异常交给EXCEPTINRESOLVER类进行处理。