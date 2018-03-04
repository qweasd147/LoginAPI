# 소셜 로그인. 

각 SNS 등에서 제공하는 소셜 로그인 REST API를 이용하여 공통된 로그인 기능을 제공하는 소스 프로그램.

각 인증 관련 REST API 서비스는 OAuth 2.0에 맞게 구현 되어야 하고, 그에 따른 REST API 패턴을 공통화 하여 다른 API(인증 관련 서비스)가 추가되어도 소스 상 변경 사항을 최소화 하는게 목적.

### * 참고 OAuth 2.0) https://tools.ietf.org/html/rfc6749#section-4.1.3

## 1. 수정사항을 최소화 하기 위하여, 각 SNS 관련 인스턴스 생성을 외부에서 관리
    <bean id="naverLogin" class="com.api.login.service.support.NaverAPI">
		<property name="serviceName" value="naver"/>
		<property name="clientId" value="${naver.client.Id}"/>
		<property name="clientSecret" value="${naver.client.secret}"/>
		<property name="redirectURL" value="${naver.callbackURL}"/>
		<property name="accesstokenEndpoint" value="${v1.naver.accesstokenEndpoint}"/>
		<property name="authorizationBaseURL" value="${v1.naver.authorizationBaseURL}"/>
	</bean>

* 위와 같이 xml에서 공통된 class를 확장하여 각 서비스 마다 다른부분만 팩토리 패턴 형태로 구현함

* bean의 id는 serviceName + "Login"으로 고정 되어야함!

## 2. 예민한 자료는 secret.properties 파일 에서 관리	
	db.driverClassName=oracle.jdbc.driver.OracleDriver
	db.url=jdbc:oracle:thin:@127.0.0.1:1521:xe
	db.id=joohyung
	db.pwd=1243
	
	naver.client.Id=xxxx
	naver.client.secret=xxxx
	naver.callbackURL=xxxx
	
	...

* 내용은 각자 알아서 발급받고 채워 넣어야함
	
* 주의! callbackURL 같은 경우 딱히 숨길 필요도 없고 100% 숨길 수도 없을 뿐더러, 소스상 고정된 callback url이 필요함.
근데 REST API KEY 발급 시, 입력하여야 되어서 그냥 secret.properties에서 관리함

## 3. 프로젝트 내 로그인 처리 과정. 요청은 당연히 외부 REST API로 요청

	3.1 code 발급 요청
	3.2 발급받은 코드로 token 발급 요청
	3.3 token을 사용하여 사용자 프로필 정보 요청
	3.4 프로필 정보와 access token을 session에 저장


## 4. API 서비스 추가에 따른 불가피한 수정 사항

> ### 4.1 위 로그인 과정을 참고하여 3.4 과정까지 진행이 완료 되어야지 최종적으로 로그인 처리 완료가 되는 것이다. 하지만 요청된 프로필 정보를 각 REST API 마다 제공해주는 정보가 다르게 제공 될 수도 있다. 이건 OAuth 2.0과 전혀 별개의 문제. 각 SNS REST API 마다 요청 받은 프로필 정보를 일관성 있게 관리를 해야하기 때문에 이 정보를  UserVo객체로 만들어 주는 역할은 어쩔 수 없는 문제라고 생각함.(추상 클래스 LoginFactory의 getUserVo 추상 메소드는 알아서 구현해야함)
>
> ### 4.2 logout 시, 각 서비스 마다 넘겨줘야하는 method type과 파라미터 키가 다르다.... 밑에 첨부 되어 있는 가이드 문서에서도 인증 관련 해서만 나와 있고 토큰을 반납 or 로그아웃 관련해서는 정의가 되어있지 않다.... 덕분에 구조가 한번 크게 바뀌었다.(추상 클래스 LoginFactory의 logoutProcess 추상 메소드는 알아서 구현해야함)

> ### 4.3 현재 프로젝트에선 아래와 같이 공통된 LoginFactory 클래스에서 각 다른 부분만 구현하는 방식을 택했다. 초기 설계 시에는 이런 부분도 구현 할 필요 없이 외부에서 설정 값만 바꾸고 모든 비지니스 로직을 똑같이 하려 했으나, 진행 할 수록 다른부분이 점차 생겨났다. 장기적으로 무슨 일이 생길지 몰라 그냥 분리 말고 다른 방법이 없었다.

	public class GoogleAPI extends LoginFactory{
	
		private static final Logger logger = LoggerFactory.getLogger(GoogleAPI.class);
		
		//TODO : host값을 어디서 초기화 해야 하는지 고민중
		private String host = "http://localhost";
		
		private String serviceName;
		private String clientId;
		private String clientSecret;
		private String redirectURL;
		private String accessTokenEndPoint;
		private String authorizationBaseURL;

		.....
	}

> ### 4.4 request.properties 파일을 확인하면 아래와 같은 정보를 확인 할 수 있다. profile은 사용자 프로필 요청 API, delete는 토큰 반납 또는 로그아웃 API를 나타낸다. 서비스 추가 시, xxx.v1.user.profile, xxx.v1.token.delete API를 추가해야 한다. (xxx는 serviceName 정보)

	naver.v1.user.profile=https://openapi.naver.com/v1/nid/me
	naver.v1.token.delete=https://nid.naver.com/oauth2.0/token?grant_type=delete&service_provider=NAVER
	kakao.v1.user.profile=https://kapi.kakao.com/v1/user/me
	kakao.v1.token.delete=https://kapi.kakao.com/v1/user/logout
	google.v1.user.profile=https://www.googleapis.com/plus/v1/people/me
	google.v1.token.delete=https://accounts.google.com/o/oauth2/revoke

## 5. 문제점

> ### 5.1 API 요청 시 scribejava 외부 라이브러리를 쓰는데 이게 디버깅이 너무 힘듦. 그냥 http 요청도 내가 직접 만들어야 하나 고민중

> ### 5.2 API 버전관리 해야함. properties에서 요청 api의 키값마다 v1 이란 정보가 있음. 이 버전 정보를 각 API 서비스 마다 관리를 할지, 아니면 프로젝트 전체적으로 관리 해야 할지 정해야함

> ### 5.3 HOST 값을 어디서 넣어 줄지 생각 해봐야함!(현재 LoginFactory 확장한 클래스에 그냥 직접 넣어져 있음)

## 6. 참고한 것들

> ### 6.1 카카오 참고 : https://devtalk.kakao.com/t/java-rest-api/14543

> ### 6.2 구글 auth Doc :
* https://developers.google.com/identity/protocols/OAuth2WebServer
* 구글 auth 한글 : https://developers.google.com/youtube/v3/guides/auth/server-side-web-apps?hl=ko

> ### 6.2 OAuth 2.0 관련 :  https://tools.ietf.org/html/rfc6749