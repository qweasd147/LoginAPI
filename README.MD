# 소셜 로그인. 

각 SNS 등에서 제공하는 소셜 로그인 REST API를 이용하여 공통된 로그인 기능을 제공하는 소스 프로그램.

각 인증 관련 REST API 서비스는 OAuth 2.0에 맞게 구현 되어야 하고, 그에 따른 REST API 패턴을 공통화 하여 다른 SNS가 추가되어도 소스 상 변경 사항을 최소화 하는게 목적.

### * 참고 OAuth 2.0) https://tools.ietf.org/html/rfc6749#section-4.1.3

## 1. 수정사항을 최소화 하기 위하여, 각 SNS 관련 인스턴스 생성을 외부에서 관리
    <bean id="naverLogin" class="com.api.login.LoginFactory">
		<property name="serviceName" value="naver"/>
		<property name="clientId" value="${naver.client.Id}"/>
		<property name="clientSecret" value="${naver.client.secret}"/>
		<property name="redirectURL" value="${naver.callbackURL}"/>
		<property name="accesstokenEndpoint" value="${v1.naver.accesstokenEndpoint}"/>
		<property name="authorizationBaseURL" value="${v1.naver.authorizationBaseURL}"/>
	</bean>

이런식으로 인스턴스를 외부에서 마구 만들어 주면 됨.

## 2. 예민한 자료는 secret.properties 파일 에서 관리	
	db.driverClassName=oracle.jdbc.driver.OracleDriver
	db.url=jdbc:oracle:thin:@127.0.0.1:1521:xe
	db.id=joohyung
	db.pwd=1243
	
	naver.client.Id=xxxx
	naver.client.secret=xxxx
	naver.callbackURL=xxxx
	
	...

내용은 각자 알아서 발급받고 채워 넣어야함
	
주의! callbackURL 같은 경우 딱히 숨길 필요도 없고 100% 숨길 수도 없을 뿐더러, 소스상 고정된 callback url이 필요함.
근데 REST API KEY 발급 시, 입력하여야 되어서 그냥 secret.properties에서 관리함

## 3. 프로젝트 내 로그인 처리 과정. 요청은 당연히 외부 SNS REST API로 요청

	3.1 code 발급 요청
	3.2 발급받은 코드로 token 발급 요청
	3.3 token을 사용하여 사용자 프로필 정보 요청
	3.4 프로필 정보와 access token을 session에 저장


## 4. SNS 추가에 따른 불가피한 수정 사항

위 로그인 과정을 참고하여 3.4 과정까지 진행이 완료 되어야지 최종적으로 로그인 처리 완료가 되는 것이다.
하지만 요청된 프로필 정보를 각 REST API 마다 다르게 제공 될 수도 있다. 이건 OAuth 2.0과 전혀 별개의 문제.
각 SNS REST API 마다 요청 받은 프로필 정보를 일관성 있게 관리를 해야하기 때문에 이 정보를  UserVo객체로 만들어 주는 역할은 어쩔 수 없는 문제라고 생각함. 

		naverLogin.setUserMethod(new UserMethod() {
			@Override
			public UserVo getUserVo(JSONObject profile) {
				
				String result = (String) profile.get("message");
				
				if(!"success".equals(result)){
					
					logger.error("통신 실패!");
					
					return null;
				};
				
				UserVo userVo = new UserVo();
				
				JSONObject respJSON = (JSONObject) profile.get("response");
				
				String id = (String) respJSON.get("id");
				String name = (String) respJSON.get("name");
				String nickName = (String) respJSON.get("nickname");
				String email = (String) respJSON.get("email");
				
				userVo.setId(id)
						.setName(name)
						.setNickName(nickName)
						.setEmail(email)
						.setServiceName("naver");
					
				return userVo;
			}
		});

현재 프로젝트에선 위와 같이 제공받은 JSONObject(프로필 정보)를 UserVo에 맞게 파싱하는 역할은 각 SNS 마다 직접 구현 및 처리해서, 일관된 UserVo객체를 만들고 있음.

## 5. 문제점

### 5.1
	로그아웃 API는 자기 멋대로임....
	네이버는 토큰만 반납 하라고 하고 kakao는 따로 REST API가 있음
	
	뭐 여기까지는 큰 차이점은 없지만 넘겨주는 Method(get, post 등)이랑 파라미터가 다름....
	
	google은 넘겨줄 파라미터 key값도 다름...
	
	위 OAuth 2.0 문서(https://tools.ietf.org/html/rfc6749) 보니깐 여기에서도 토큰 반납하는 건 따로 안나와 있음
	
	18.02.27///생각보다 oauth 2.0에 명시되어 있지 않는 부분은 차이점이 많아서 일단 기본 베이스에 다른부분만 각자 구현해서
	
	찍어내기로(팩토리 패턴) 해야할꺼 같음

### 5.2
	API 요청 시 scribejava 외부 라이브러리를 쓰는데 이게 디버깅이 너무 힘듦.
	그냥 http 요청도 내가 직접 만들어야 하나 고민중

### 5.3
	API 버전관리 해야함.	

### 6. 참고한 것들
카카오 참고 : https://devtalk.kakao.com/t/java-rest-api/14543

구글 auth Doc : https://developers.google.com/identity/protocols/OAuth2WebServer
구글 auth 한글 : https:https://developers.google.com/youtube/v3/guides/auth/server-side-web-apps?hl=ko