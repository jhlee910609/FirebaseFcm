# 170705 Cloud messaging in Firebase

### 1. 개념

- 클라이언트에게 최신화된 정보, 경고 등 알림을 원할 때 사용할 수 있도록 Firebase에서 제공하는 `일종의 알림 서비스`이다.



### 2. 구현 방법

##### 1. 안드로이드 스튜디오에서 `Firebase`를 연동시킨다.

- 안드로이드 스튜디오 오른편의 `assistance`의 설명과 관련 코드를 참고하면 된다.

![](https://ws2.sinaimg.cn/large/006tKfTcgy1fh9fpo9to4j31kw0zk1cn.jpg)



##### 2. `MyFirebaseInstanceIDService.java` 코드 분석

-  파이어베이스와 연동된 app은 최초 설치 시, 아래 `MyFirebaseInstanceIDService.java` 에서 token을 생성한다. 
-  초기 생성된 token은 앱 삭제 후, 재설치 하지 않는 이상 고유 번호처럼 유지된다. 

```java
// FirebaseInstanceIdService를 상속받아야 한다. 
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
	
  	// 파이어베이스 데이터베이스 생성 및 reference 생성한다. 
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference uidRef = database.getReference("uid");

    @Override
    public void onTokenRefresh() {
        // 아래 코드가 최초 app 설치 시, token이 생성되는 코드다.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
      
        // 생성된 token을 sendRegistrationToServer(); 를 통해 파이어베이스 서버로 전송한다.
        sendRegistrationToServer(refreshedToken);
    }
  
    public void sendRegistrationToServer(String token) {

        String deviceUid = Settings.Secure.getString(
                getContentResolver(), Settings.Secure.ANDROID_ID
        );
      	// 내가 작성한 Uid 객체 생성자에 값들을 넣어 uid를 초기화한 후, uid 객체 자체를 파이어베이스 서버로 보낸다. 
      	// 이때 주의할 점은 파이어베이스의 경우, default 생성자가 꼭 필요하므로 Uid.class에 꼭 작성해준다.
        Log.e("UUID ----", deviceUid + "");
        Uid uid = new Uid(deviceUid, "준희", token);
        uidRef.child(deviceUid).setValue(uid);
    }
}
```



##### 3. `AndroidManifest.xml`에 `<service>` 를 추가해준다.

- 매니페스트에 해당 서비스를 등록하지 않으면 `MyFirebaseInstanceIDService.class` 가 실행되지 않아 token을 발행받을 수 없다.

```xml
 <service android:name=".MyFirebaseInstanceIDService">
            <intent-filter>
                <action android:name="com.google.firebase.INSTANCE_ID_EVENT" />
            </intent-filter>
 </service>
```



### 3. node.js와 연동하기

##### 3.1. cloud messaging server key 발급받기

- node.js와 연동하기 위해서는 `cloud messaging server key`가 필요하다.
- 아래 그림을 따라 가면 server key 구하는 것은 어렵지 않다.

![](https://ws2.sinaimg.cn/large/006tKfTcgy1fh9fqs9xdyj311y0lcwic.jpg)

![](https://ws2.sinaimg.cn/large/006tKfTcgy1fh9fr5ewwhj311y0lcwir.jpg)

##### 3.2. node.js로 간단한 web server 구축하기 [소스코드 보기]

- node.js와 firebase를 연동하기 위해 간단한 web server를 node.js로 구축했다.
- 이 프로젝트에서 node.js의 역할은 client와의 http 통신 후, response(message 객체의 내용)를 웹 브라우져에 보여주는 정도이다.

```javascript
[server.js 파일]

// ------- fcm 설정 -------
// fcm과 연동하기 위해선 아래 2개의 값을 정의해야 한다.

// 1. fcm api url
var fcmUrl = "https://fcm.googleapis.com/fcm/send"; 

// 2. 위에서 발급받은 fcm server key
var serverKey = "AAAA9K6-ArA:APA91bGIKHd0XguAQJ1BYKc31mUdktumEI0CI1t1NJhkBFxNO8Zp9ruHj7O5tLXqSVFWo7T5X9P848LrZdwlhwtwjBw0kiUxSAg69XBm8ym1O_kWkSc3ainkaTjtDpZaZyQSnmXJubST";

//... 아래 소스 코드 계속 

```

