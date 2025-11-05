🏰 game-character (RTS Game Character Simulation)
이 프로젝트는 중세 시대를 배경으로 한 RTS 게임에서
캐릭터(Unit)들의 이동 및 공격, 수송 행동을 객체지향적으로 구현한
Kotlin + Gradle 기반 시뮬레이션 프로그램입니다.

🎮 캐릭터 구성
캐릭터	이동 방식	공격 여부	공격 방식	비고
Knight	말을 타고 이동	가능	창으로 근접 공격 (공중 공격 불가)	Shuttle 탑승 가능
Archer	걸어서 이동	가능	화살 공격 (지상·공중 모두 가능)	Shuttle 탑승 가능
Griffin	날아서 이동	가능	하늘에서 번개 공격 (공중 공격 불가)	Shuttle 탑승 불가
Shuttle	날아서 이동	불가능	없음	Knight/Archer 최대 8기 탑승 가능

🏗️ 설계 구조
모든 캐릭터는 GameCharacter 추상 클래스를 상속
공격이 가능한 유닛만 Attackable 인터페이스 구현
이동 기능은 Movable 인터페이스로 분리
Shuttle 은 공격 기능이 없고, 대신 수송 기능(board(), unloadAll())을 수행
제네릭(Generic) 을 사용해 Shuttle이 특정 유닛(GroundUnit)만 수송 가능하도록 제한

⚙️ 클래스 다이어그램 (요약)
GameCharacter (추상 클래스)
 ├── GroundUnit (지상 유닛)
 │    ├── Knight : 근거리 공격 (창)
 │    └── Archer : 원거리 공격 (화살)
 ├── Griffin : 공중 유닛 (번개 공격)
 └── Shuttle : 수송 유닛 (Transporter<GroundUnit>)

🚀 실행 시나리오
유닛 생성
Knight 16기, Archer 16기, Shuttle 4대, Griffin 5기 생성
탑승 및 이동
Shuttle에 Knight 4기, Archer 4기씩 탑승
Shuttle과 Griffin이 좌표 (10, 10) 으로 이동
전투 및 하차
탑승 유닛 하차 후 공격 테스트 수행

💬 예시 출력
Knight1가 Shuttle1에 탑승합니다.
Shuttle1이 (10, 10)으로 날아갑니다.
Knight1은 Griffin2를 공격할 수 없습니다. (공중 유닛)
Archer1이 Griffin2에게 화살을 발사합니다.
Griffin1이 Archer2에게 번개를 내리칩니다.

🧠 설계 원칙 적용
원칙	적용 내용
ISP	이동(Movable), 공격(Attackable) 기능을 인터페이스로 분리
DRY	Shuttle·Transporter 구조를 제네릭으로 통합
OCP	새로운 유닛 추가 시 기존 코드 수정 없이 확장 가능
타입 안정성	Shuttle에는 지상 유닛만 탑승 가능

🛠️ 기술 스택
Language: Kotlin
Build Tool: Gradle
Concepts: 추상 클래스, 인터페이스, 제네릭, 상속, 오버라이딩
Paradigm: 객체지향 프로그래밍 (OOP)

📂 파일 구조
src/
 ├── Main.kt              // 메인 실행 및 테스트 코드
 ├── GameCharacter.kt     // 추상 클래스
 ├── Knight.kt, Archer.kt // 지상 유닛
 ├── Griffin.kt           // 공중 유닛
 └── Shuttle.kt           // 수송 유닛

🎯 학습 목표
객체지향 원칙(ISP, DRY, OCP) 실습
클래스 상속 및 인터페이스 활용
코드 중복 최소화 및 확장성 확보
타입 안전한 구조 설계 경험
