🚀 중세시대 RTS - 게임 캐릭터 구현 과제 (game-character)

이 프로젝트는 Kotlin과 Gradle을 사용한 객체 지향 프로그래밍(OOP) 과제입니다.
중세시대를 배경으로 Knight, Archer, Griffin, Shuttle 네 가지 유닛의 기본 행동(이동, 공격, 탑승/하차)을 객체 지향 원칙에 따라 구현합니다.


🎯 핵심 설계 원칙

이 코드는 평가 기준인 재사용성, 확장성, 중복 코드 최소화를 다음 원칙을 통해 달성하고자 했습니다.

-ISP (인터페이스 분리 원칙)

a. Movable: 이동 가능한 모든 유닛(Knight, Archer, Shuttle, Griffin)이 구현하는 인터페이스.

b. Attackable: 공격 가능한 유닛(Knight, Archer, Griffin)만 선택적으로 구현하는 인터페이스.

-DRY (중복 제거 원칙)

a. Transporter<T>: Shuttle의 공통 로직(탑승, 하차, 승객 위치 동기화)을 제네릭 추상 클래스로 분리하여 코드 중복을 제거했습니다.

-타입 안전성 (Type Safety)

a. GroundUnit: Knight와 Archer의 공통 부모 추상 클래스를 생성했습니다.

b. Shuttle이 Transporter<GroundUnit>을 상속받게 하여, Griffin 등 다른 유닛이 실수로 탑승하는 것을 컴파일 시점에 방지했습니다.


⚙️ 실행 환경

JDK 11 이상 (Kotlin 1.9.x 호환)

Gradle 7.x 이상


▶️ 프로그램 실행 방법

이 프로젝트는 Gradle을 통해 빌드 및 실행됩니다.

1. 프로젝트 클론

git clone <Your-Repository-URL>/game-character.git
cd game-character


2. Gradle로 실행 (권장)

터미널에서 다음 명령어를 입력하여 main 함수를 실행합니다.

./gradlew run


참고: 위 명령어가 동작하려면 build.gradle.kts (또는 build.gradle) 파일에 application 플러그인이 설정되어 있고, mainClass가 GameCharacterKt (파일 기반 main 함수)로 지정되어 있어야 합니다.

3. IDE에서 직접 실행

가장 간단한 방법입니다. IntelliJ IDEA와 같은 IDE에서 game_character.kt 파일을 열고, main 함수 옆의 '▶' (실행) 버튼을 클릭합니다.


🖥️ 실행 결과 예시

프로그램을 실행하면 main 함수에 정의된 시나리오에 따라 유닛 생성, 탑승, 이동, 하차, 공격 테스트 결과가 콘솔에 순서대로 출력됩니다.

= Game Start =
Knight1가 Shuttle1에 탑승합니다.
Archer1가 Shuttle1에 탑승합니다.
Knight2가 Shuttle1에 탑승합니다.
Archer2가 Shuttle1에 탑승합니다.
... (모든 유닛 탑승) ...

=== 3. 셔틀 4대와 Griffin 5기 이동 ===
Shuttle1가 날아서 (10, 10)로 이동합니다.
Shuttle2가 날아서 (10, 10)로 이동합니다.
Shuttle3가 날아서 (10, 10)로 이동합니다.
Shuttle4가 날아서 (10, 10)로 이동합니다.
Griffin1이(가) 날아서 (10, 10)로 이동합니다.
... (모든 유닛 이동) ...

=== 4. 셔틀에서 모든 캐릭터 하차 ===
Shuttle1가 모든 승객을 내립니다:
Knight1가 내립니다.
Archer1가 내립니다.
... (모든 유닛 하차) ...

=== 5. Knight 1기 공격 테스트 ===
--- Knight1의 공격 ---
Knight1가 Knight2를 창으로 찌릅니다.
Knight1가 Archer2를 창으로 찌릅니다.
Knight1가 Griffin2를 공격 할 수 없습니다. (공중 유닛)
Knight1가 Shuttle2를 공격 할 수 없습니다. (공중 유닛)

... (Archer, Griffin 공격 테스트) ...

=== 시나리오 종료 ===
