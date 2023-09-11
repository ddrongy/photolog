## 회원 API

### 기본 기능

1. 회원 가입
2. 회원 로그인
3. 회원 로그아웃
4. 회원 수정

 

---

## 여행 API

### 기본 규칙

1. 사진 입력은 연달아 들어올 수 있음
    - 연결된 일정이 아닌 사진이 들어온 경우 “정말 여행 사진이 맞으신가요?” 같은 문구 return
2. 여행 삭제시 연결된 테이블(days, locations, days) 전부 삭제 (Cascade설정) 
3. 본인 여행만 본인이 수정 및 삭제 가능해야함 

### 기본 기능

1. travel 생성(추가) 
    - 여행 등록 버튼과 함께 user와 연결된 빈 travel 객체 생성
2. travel 에서 사진 받은 후 자동 일정 분석 
    - 이전에 photo-travel 연결되서 생성된 객체 정보를 가지고 일정 분석, 값 update 진행
3. 여행 title 설정, theme 설정, location 설정 
    - 페이지당 한번씩 처리
    - theme 변경 시 + 연결된 article이 있는 경우 article의 theme도 같이 변경해줘야 함
4. text summary, map summary 
    - 혹시나 추가로 더 들어가는 정보 있다면 수정 진행 (현재 text sum에는 Urls로 전달중)
5. 내 여행 로그 불러오기  →  지금  N+1 문제 발생 - entityGraph, fetchJoin 
6. travel 삭제

### 부가 기능

1. 사진 다중 선택 → meta 정보 분석을 통해 [days, locations] 기준으로 grouping
2. 여행로그의 [제목] → 각 locations 별 [장소명, 장소 설명] 받아야 함 
3. 장소명 fullAddress 표기 방식 
    - 서울특별시 / 서초구, ~동, 상세주소
    - 서울특별시 서초구 ~동 상세주소
4. 기본 입력정보 받은 후에 전체 정리 페이지 [text view, map view]
    - map view의 경우 전체 locations를 days 기준으로 grouping
5. location view 에서 사진
    - 삭제 기능
    - 해당 Location 외에 다른 location으로의 이동 가능해야 함 ⇒ 사진 API에서 확인 가능

---

## 사진 API

### 기본 기능

1. travel에 photo 추가  
2. photo에 연결된 location 정보 변경 
3. photo에 연결된 Location 외에 장소 조회 
4. 사진 태그기반 검색 

### 부가 기능

1. 본인 것만 calculate 가능해야함 
2. 본인 것만 삭제 가능해야함 
3. 사진 태그 자동생성 + ml 모델 부착 ⇒ 형식 변경 + 한번에 imgurl list 전달하고 한번에 받는걸로 변경(시간 너무 오래 걸린다는 문제 ) 

---

## **게시글 API**

### 기본 규칙

1. 한 여행당 하나의 게시글만 생성 가능   
2. 여행 삭제시 자동으로 게시글도 삭제 (Cascade설정)   
3. 태그 (자기가 적을 수 있도록) 
4. 좋아요/북마크 추가/삭제시 user/bookmark/article 동일하게 움직이는 로직이어야 함 
5. 본인 여행 로그에서만 추가로 게시글 생성 및 본인만 수정 및 삭제 가능해야함 
6. **budget과 theme**은 travel과 연동된 상태 (article에서 별도 수정 불가) 

### 기본 기능

1. 여행 생성 후에 여행 페이지 내에 게시글 작성으로 작성 가능 
    - 게시글 작성 버튼 → 현재 내 여행 중 작성 가능한 게시글만 표시해줘도 좋을 것 같긴함
2. 게시글 작성시 여행 기록 정보를 그대로 받아오는 기능 추가 (getMyLogInfo 추가) 
    - location에 description/content 두가지로 분류해서 게시글일때는 location의 content를 받아옴
3. 게시글 저장  
    - travel 당 1개의 게시글만 설정 가능
    - 초기 저장시 travel정보 그대로 받아가서 텍스트 띄움
        - 여행 title
        - location의 description(not content)
4. 게시글 수정 
    - 제목, summary , LocationContent, bucket 한번에 변경 처리
    - bucket: 원 단위로 저장 → 앱 내에서는 간편하게 버튼으로 약(20만원 단위로 설정 가능)
    - theme의 경우 별도 불러오기 없이 travel 정보에서 가져오기(사실상 content, budget 제외 기본정보 default setting)
    - 이외 travel Info GET: travel title, location description
5. 게시글 삭제  
    - 삭제 시 travel, user의 article에서도 삭제되야함
6. budget 저장 
    - 1원 단위로 저장 → 앱 내에서는 간편하게 버튼으로 약 20만원 단위로 설정 가능
- hide 0인 글만 처리 hide 처리된 글은 볼 수 없어야함
    - 어떤 경우던지 신고된 글을 조회할 경우 “광고/홍보글 신고로 인해 숨김 처리된 글입니다”
- photo: photo객체 내에 article Boolean true만 보여주기
- 조건 별 조회 기능
    - user 별

### 부가기능

1. 공감
    - articleLikeCount를 따로 저장하는게 좋은가 ? → 성능 향상을 위해 별도 진행
        - 따로 저장해두면 당연히 성능 향상 but 일관성 유지의 문제가 발생, 복잡성 증가의 위험
2. 북마크 
    - **BookmarkService를 만들어 분리하는 경우:** 북마크 추가, 삭제, 조회 등의 기능을 BookmarkService에 넣는 것이 좋음, Bookmark는 User와 Article 사이의 독립적인 관계를 나타내기 때문에 분리된 서비스를 만들어서 관리하는 것이 유지보수 관점에서 더 좋음
    - 북마크 안에서 필터링, 내북마크 정보 가져오기 기능
3. 공유
    - 어떤걸 공유하는건지?
        - 공유 url, qr코드, 소셜SNS로의 공유(인스타, 카톡)
4. 신고
    - 여행 광고 등의 글에 신고 가능
    - 신고 누적 5회의 경우 광고/홍보글로 선정 → 글 hide 처리
    - hide된 글은 관리자가 후에 판별 후 삭제/복구 조치 취함
    - userID별로 저장해두고 같은 인원이 계속 신고 할  수 없어야함. 이미 신고처리된 경우 이미 신고된 글입니다를 내보내야 함
5. 태그 
6. 예산
    - 원 단위
7. 사진 
    - Photo Table에서 article(Boolean) 값이 true로 설정된 경우만 볼 수 있음
    - photo hide 시키는 controller 작성
        
        

---

## TourData API

### 기본 규칙

1. 관광지 태그검색에 사용
2. 관광지 위치검색에 사용

### 기본 기능

1. 관광지 정보를 포함한 객체를 사용 
2. 관광지별 대>중>소 분류 
    - 오퍼레이션 명세 돌려야함

1. 관광지 북마크 기능 
2. 관광지 검색(%like%) → 인덱스 기반검색으로 변경
