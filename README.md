### （0day）DBSyncer后台自定义插件上传-注入内存马
> DBSyncer（简称dbs）是一款开源的数据同步中间件，提供MySQL、Oracle、SqlServer、PostgreSQL、Elasticsearch(ES)、Kafka、File、SQL等同步场景。支持上传插件自定义同步转换业务，提供监控全量和增量数据统计图、应用性能预警等。

0x01 弱口令爆破登录后台

0x02 登录后台上传插件
![img.png](img/img.png)

0x03 注入内存马执行命令
```java
http://localhost:18686/?cmd=whoami
```
![img.png](img/img-rce.png)

**感谢一起研究的师傅** :confetti_ball:

<table>
    <tr>
        <td align="center"><img alt="testnet0" src="https://avatars.githubusercontent.com/u/18673513?v=4" style="width: 100px;" /><br /><a href="https://github.com/testnet0">@testnet0</a></td>
        <td align="center"><img alt="Nacl" src="https://avatars.githubusercontent.com/u/76427657?v=4" style="width: 100px;" /><br /><a href="https://github.com/Dr-dot-droid">@Nacl</a></td>
    </tr>
</table>

---
