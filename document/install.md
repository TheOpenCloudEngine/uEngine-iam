# OCE IAM

## Installation

### Install CouchDB

```
$ sudo apt-get install software-properties-common -y

$ sudo add-apt-repository ppa:couchdb/stable -y

$ sudo apt-get update -y

# remove any existing couchdb binaries
$ sudo apt-get remove couchdb couchdb-bin couchdb-common -yf

$ sudo apt-get install -V couchdb

  Reading package lists...
  Done Building dependency tree
  Reading state information...
  Done
  The following extra packages will be installed:
  couchdb-bin (x.y.z0-0ubuntu2) couchdb-common (x.y.z-0ubuntu2) couchdb (x.y.z-0ubuntu2)

# manage via upstart for 14.04
$ sudo stop couchdb
  couchdb stop/waiting
  
# update /etc/couchdb/local.ini with 'bind_address=0.0.0.0' as needed
$ sudo start couchdb
  couchdb start/running, process 3541

# manage via upstart for 14.04
$ sudo stop couchdb
  couchdb stop/waiting
  
# update /etc/couchdb/local.ini with 'bind_address=0.0.0.0' as needed
$ sudo start couchdb
  couchdb start/running, process 3541

# manage via systemd for 15.10 and newer
$ sudo systemctl stop couchdb

# update /etc/couchdb/local.ini with 'bind_address=0.0.0.0' as needed
# or add 'level=debug' to the [log] section
$ sudo systemctl start couchdb

# systemd is not very chatty so lets get a status
$ sudo systemctl status couchdb
● couchdb.service - Apache CouchDB
   Loaded: loaded (/lib/systemd/system/couchdb.service; enabled; vendor preset: enabled)
   Active: active (running) since Sun 2016-01-31 23:50:50 UTC; 5s ago
 Main PID: 3106 (beam.smp)
   Memory: 20.3M
      CPU: 394ms
   CGroup: /system.slice/couchdb.service
           ├─3106 /usr/lib/erlang/erts-7.0/bin/beam.smp -Bd -K true -A 4 -- -root /usr/lib/erlang -progname erl -- -home /var/lib/couchdb -- -noshell -noin...
           └─3126 sh -s disksup

Jan 31 23:50:50 u1 systemd[1]: Started Apache CouchDB.
Jan 31 23:50:50 u1 couchdb[3106]: Apache CouchDB 1.6.1 (LogLevel=info) is starting.
Jan 31 23:50:51 u1 couchdb[3106]: Apache CouchDB has started. Time to relax.
Jan 31 23:50:51 u1 couchdb[3106]: [info] [<0.33.0>] Apache CouchDB has started on http://127.0.0.1:5984/

```

### Install Maven and Tomcat

```
$ sudo apt-get install openjdk-7-jdk

$ sudo apt-get install maven

$ sudo apt-get install tomcat7
```

### Download

```
$ git clone https://github.com/TheOpenCloudEngine/oceIAM
```

### Database Configuration

다운받은 소스 코드의 oceIAM/iam-web/src/main/webapp/WEB-INF/config.properties 경로의 파일을 수정합니다.

```
$ sudo vi oceIAM/iam-web/src/main/webapp/WEB-INF/config.properties

.
.
###########################################
## DataSource Configuration
###########################################

couch.db.url=http://192.168.135.181:5984
couch.db.username=
couch.db.password=
couch.db.database=forcs_iam
couch.db.autoview=true
```

 - couch.db.url : 카우치db 호스트
 
 - couch.db.username : 카우치db 사용자
 
 - couch.db.password : 패스워드
 
 - couch.db.database : 데이터 베이스 이름
 
 - couch.db.autoview : 뷰 오토 제너레이션
 
카우치 db에 별도의 인증을 설정하지 않았다면 username 과 password 는 공란입니다. 

### Mail Configuration

```
##########################################
## JavaMail Configuration
###########################################

mail.host=smtp.gmail.com
mail.username=
mail.password=
mail.port=587
mail.smtp.auth=true
mail.smtp.starttls.enable=true
mail.from.address=${mail.username}
mail.from.name=uengine
mail.html=false

mail.contacts.address=sppark@uengine.org
mail.redirect.address=http://localhost:8080
```

매니지먼트 사용자 가입 이메일, 패스워드 변경 이메일을 보낼 때 쓰이는 설정입니다.

 - mail.host : 메일 서비스 호스트
 
 - mail.username : 사용자 이름
 
 - mail.password : 패스워드
 
 - mail.port : 메일 서비스 포트
 
 - mail.from.address : 발송하는 이메일에 표시될 From
 
 - mail.from.name : 발송하는 이메일에 표시될 이름
 
 - mail.contacts.address : 문의 메일을 받을 주소
 
 - mail.redirect.address : 발송하는 이메일의 링크 주소
  
mail.redirect.address 는 IAM 이 설치된 서버의 도메인 주소 또는 아이피를 표기하도록 합니다.
 

### Build and Launch

```
$ sudo service tomcat7 stop

$ sudo rm -rf /var/lib/tomcat7/webapps/ROOT
$ sudo rm -rf /var/lib/tomcat7/webapps/ROOT.war 

$ cd oceIAM

$ mvn clean install

$ sudo cp iam-web/target/iam-web-0.0.1-SNAPSHOT.war /var/lib/tomcat7/webapps/

$ sudo mv /var/lib/tomcat7/webapps/iam-web-0.0.1-SNAPSHOT.war /var/lib/tomcat7/webapps/ROOT.war

$ sudo chmod +x /var/lib/tomcat7/webapps/ROOT.war

$ sudo service tomcat7 start
```

초기 로그인은 support@iam.co.kr  /   admin  입니다.







