// 중세시대 RTS 게임 캐릭터 구현 과제
// 1. 인터페이스 분리 (ISP):
//    - Movable: 이동 가능한 모든 유닛이 구현 (Knight, Archer, Shuttle, Griffin)
//    - Attackable: 공격 가능한 유닛만 구현 (Knight, Archer, Griffin)
//
// 2. 중복 코드 제거 (DRY):
//    - Transporter<T>: Shuttle의 공통 로직 (탑승, 하차, 이동)을 제네릭 클래스로 분리
//    - GroundUnit: Shuttle에 탑승 가능한 유닛(Knight, Archer)의 공통 타입을 묶기 위한 부모 클래스
//
// 3. 타입 안전성 (Type Safety):
//    - Shuttle은 Transporter<GroundUnit>을 상속받아, Knight와 Archer만 탑승 가능
//
// 4. 스크린샷 출력 일치:
//    - HP/데미지 계산 로직은 스크린샷에 보이지 않으므로, attack 메서드는 문자열 출력만 수행
//    - main 함수의 유닛 생성, 탑승, 이동, 하차, 공격 순서 및 출력 문자열을 스크린샷과 일치시킴

// 1. 좌표 표현 (Data Class)
/**
 * 2차원 좌표(x, y)를 표현하는 데이터 클래스입니다.
 * toString()을 오버라이드하여 "(x, y)" 형태로 출력합니다.
 *
 * [WHY] 'data class'를 사용한 이유:
 * 'Point'는 데이터를 저장하는 것이 주 목적인 클래스입니다.
 * 'data class'로 선언하면, 'toString()', 'equals()', 'hashCode()' 등
 * 데이터 비교 및 표시에 필요한 메서드들을 Kotlin 컴파일러가 자동으로 생성해주어 코드가 매우 간결해집니다.
 */
data class Point(var x: Int, var y: Int) {
    override fun toString(): String = "($x, $y)"
}

// 2. 공통 인터페이스 (Interface Segregation)
/**
 * 이동 가능한 유닛(Movable)이 구현해야 하는 인터페이스입니다.
 * - position: 현재 위치
 * - moveTo(): 지정된 위치로 이동하는 기능
 *
 * [WHY] 'interface'를 사용한 이유 (ISP - 인터페이스 분리 원칙):
 * '이동'은 유닛이 가질 수 있는 하나의 '기능' 또는 '계약'입니다.
 * 이를 인터페이스로 분리하면, 'Movable'을 구현(implements)하는 모든 클래스(Knight, Archer 등)는
 * 반드시 'moveTo' 기능을 제공해야 함을 강제할 수 있습니다.
 */
interface Movable {
    var position: Point
    fun moveTo(dest: Point)
}

/**
 * 공격 가능한 유닛(Attackable)이 구현해야 하는 인터페이스입니다.
 * - canAttackAir: 공중 유닛 공격 가능 여부 (true/false)
 * - attackType: 공격 방식 (출력용 문자열, 예: "창", "화살")
 * - attack(): 대상을 공격하는 기능
 *
 * [WHY] 'interface'를 사용한 이유 (ISP - 인터페이스 분리 원칙):
 * '공격' 기능도 '이동'과 마찬가지로 모든 유닛이 갖는 기능이 아닙니다. (예: Shuttle은 공격 불가)
 * 'Attackable' 인터페이스를 따로 분리하여, 공격이 가능한 유닛(Knight, Archer, Griffin)만
 * 선택적으로 이 인터페이스를 구현하도록 합니다. 이것이 '인터페이스 분리 원칙(ISP)'의 핵심입니다.
 */
interface Attackable {
    val canAttackAir: Boolean
    val attackType: String
    fun attack(target: GameCharacter)
}

// 3. 추상 클래스 (Abstraction & Inheritance)

/**
 * 게임 내 모든 캐릭터(유닛)의 기본이 되는 추상 클래스입니다.
 * Movable 인터페이스를 구현하여 모든 유닛이 이동 기능을 갖도록 강제합니다.
 * - name: 유닛의 이름 (예: "Knight1")
 * - isAirUnit: 공중 유닛 여부 (true/false)
 * - position: 현재 위치 (Movable 구현)
 *
 * [WHY] 'abstract class' (추상 클래스)를 사용한 이유 (상속):
 * 모든 유닛(Knight, Archer, Shuttle, Griffin)은 'name', 'isAirUnit', 'position'이라는
 * 공통된 속성(데이터)을 가집니다. 또한 'Movable'이라는 공통된 계약도 따릅니다.
 * 이 공통점들을 'GameCharacter'라는 하나의 '추상 부모 클래스'로 묶어 상속받게 하면,
 * 중복 코드를 줄이고 모든 유닛을 일관성 있게 관리할 수 있습니다.
 * 'abstract' 키워드는 이 클래스 자체로는 객체를 만들 수 없고(예: new GameCharacter() 불가),
 * 반드시 하위 클래스(Knight 등)로 구체화되어야 함을 의미합니다.
 */
abstract class GameCharacter(
    val name: String,
    val isAirUnit: Boolean, // true면 공중 유닛, false면 지상 유닛
    override var position: Point = Point(0, 0)
) : Movable {
    // [WHY] 'abstract override fun moveTo':
    // GameCharacter는 Movable 인터페이스를 구현(implements)하지만,
    // 'moveTo'의 실제 내용은 유닛마다 다릅니다. (Knight: 말을 타고, Archer: 걸어서)
    // 따라서 'moveTo'의 구현을 하위 클래스(Knight, Archer)에게 다시 위임하기 위해 'abstract'로 선언합니다.
    abstract override fun moveTo(dest: Point)
}

/**
 * '지상 유닛'을 표현하는 추상 클래스입니다. (Knight, Archer가 상속)
 * GameCharacter를 상속받으며, isAirUnit을 false로 고정합니다.
 *
 * [WHY] 'GroundUnit'이라는 중간 추상 클래스를 만든 이유 (타입 안전성):
 * 요구사항에 따르면 Shuttle은 'Knight'와 'Archer'만 태울 수 있습니다.
 * 이 둘의 공통점은 '지상 유닛'이라는 것입니다.
 * 이 둘을 묶어줄 'GroundUnit'이라는 공통의 부모 타입을 만들면,
 * Shuttle의 제네릭 타입을 'Transporter<GroundUnit>'으로 지정할 수 있습니다.
 * 이렇게 하면 Shuttle은 'GroundUnit'의 하위 클래스인 Knight와 Archer만 태울 수 있고,
 * Griffin 등 다른 유닛은 탑승을 시도하면 컴파일 오류가 발생합니다. (타입 안전성 확보)
 */
abstract class GroundUnit(
    name: String,
    position: Point = Point(0, 0)
) : GameCharacter(name, false, position) // 지상 유닛이므로 isAirUnit = false

// 4. Transporter 제네릭 클래스 (Generics & DRY)

/**
 * 수송선(Transporter)의 공통 기능을 구현한 제네릭 추상 클래스입니다.
 * GameCharacter를 상속받으며, isAirUnit을 true(공중 유닛)로 고정합니다.
 *
 * [WHY] '제네릭(Generics) <T>'를 사용한 이유 (DRY - 중복 제거 원칙):
 * Shuttle은 GroundUnit을 태우고, (Starcraft의) Dropship은 Marine을 태웁니다.
 * 어떤 유닛을 태우든, '유닛을 태우고(board)', '내리고(unloadAll)',
 * '수송선이 이동할 때 탑승 유닛도 같이 이동하는(moveTo)' 핵심 로직은 100% 동일합니다.
 *
 * 제네릭 <T>를 사용하면, 이 공통 로직을 'Transporter'라는 클래스에 '한 번만' 작성할 수 있습니다.
 * 그리고 'T'라는 자리에 'GroundUnit'을 넣으면 Shuttle이 되고, 'Marine'을 넣으면 Dropship이 됩니다.
 * 이것이 '반복하지 말라(DRY)' 원칙을 제네릭으로 해결한 것입니다.
 * 'T : GameCharacter'는 'T' 자리에 'GameCharacter'의 하위 클래스만 올 수 있도록 제한하는 문법입니다.
 */
abstract class Transporter<T : GameCharacter>(
    name: String,
    position: Point,
    val capacity: Int
) : GameCharacter(name, true, position) { // 수송선은 항상 공중 유닛

    protected val cargo = mutableListOf<T>()

    /**
     * 유닛(unit)을 수송선에 태웁니다.
     * [WHY] 이 'board' 함수는 제네릭 타입 T로 선언되어,
     * Transporter<GroundUnit>을(를) 상속받은 Shuttle은 GroundUnit 타입만 매개변수로 받을 수 있습니다.
     */
    fun board(unit: T) {
        if (cargo.size >= capacity) {
            println("${name}: 정원 초과로 ${unit.name} 탑승 불가.")
            return
        }
        cargo.add(unit)
        println("${unit.name}가 ${name}에 탑승합니다.")
        // 탑승한 유닛의 위치를 수송선 위치로 즉시 동기화
        unit.position = this.position
    }

    /**
     * 수송선에 탑승한 모든 유닛을 내립니다.
     * [WHY] 이 'unloadAll' 함수는 모든 수송선이 공통으로 사용하는 기능입니다.
     */
    fun unloadAll() {
        println("${name}가 모든 승객을 내립니다:")
        cargo.forEach { println("${it.name}가 내립니다.") }
        cargo.clear() // 모든 유닛을 리스트에서 제거
    }

    /**
     * 수송선이 지정된 위치(dest)로 이동합니다.
     * [WHY] 'override'를 사용: 부모인 GameCharacter의 'moveTo'를 '날아서 이동'하도록 구현했습니다.
     * 또한, 수송선의 핵심 기능인 '탑승자 위치 동기화' 로직이 여기에 포함됩니다.
     */
    override fun moveTo(dest: Point) {
        position = dest
        println("${name}가 날아서 ${dest}로 이동합니다.")
        // 탑승자 위치 동기화 (수송선이 이동하면, 승객도 같은 위치로 이동)
        cargo.forEach { it.position = dest }
    }
}

// 5. 실제 유닛 구현 (Concrete Classes)

/**
 * Knight (기사)
 * - GroundUnit을(를) 상속받는 지상 유닛
 * - Attackable을(를) 구현 (공격 가능)
 * - canAttackAir = false (공중 공격 불가)
 *
 * [WHY] 'Knight' 클래스를 만든 이유:
 * 'Knight'는 'GroundUnit'(부모)의 속성을 물려받고,
 * 'Movable'과 'Attackable' (인터페이스)의 기능을 구체적으로 구현하는 '실체 클래스'입니다.
 */
class Knight(name: String) : GroundUnit(name), Attackable {
    // Attackable 인터페이스 구현
    override val canAttackAir = false // 날아다니는 캐릭터 공격 불가
    override val attackType = "창" // 스크린샷의 "창으로 찌릅니다"에 맞춤

    /**
     * Knight의 이동 방식 (Movable 구현)
     * [WHY] 'override'를 사용: 부모인 GameCharacter의 추상 메서드 'moveTo'를
     * 'Knight'에 맞게 '말을 타고 이동'하는 구체적인 내용으로 구현합니다.
     */
    override fun moveTo(dest: Point) {
        position = dest
        println("${name}이(가) 말을 타고 ${dest}로 이동합니다.")
    }

    /**
     * Knight의 공격 (Attackable 구현) - 스크린샷 출력에 맞춤
     * [WHY] 'override'를 사용: Attackable 인터페이스의 'attack' 기능을
     * '창으로 찌르기'라는 구체적인 내용과 '공중 공격 불가' 로직으로 구현합니다.
     */
    override fun attack(target: GameCharacter) {
        // 1. 공중 유닛 공격 시도 여부 확인 (핵심 로직)
        if (target.isAirUnit && !canAttackAir) {
            println("${name}가 ${target.name}를 공격 할 수 없습니다. (공중 유닛)")
            return
        }
        // 2. 공격 실행 (출력)
        println("${name}가 ${target.name}를 ${attackType}으로 찌릅니다.")
    }
}

/**
 * Archer (궁수)
 * - GroundUnit을(를) 상속받는 지상 유닛
 * - Attackable을(를) 구현 (공격 가능)
 * - canAttackAir = true (지상/공중 모두 공격 가능)
 */
class Archer(name: String) : GroundUnit(name), Attackable {
    // Attackable 인터페이스 구현
    override val canAttackAir = true // 땅, 하늘 모든 곳 공격 가능
    override val attackType = "화살" // 스크린샷의 "화살로 공격합니다"에 맞춤

    /**
     * Archer의 이동 방식 (Movable 구현)
     * [WHY] 'override'를 사용: 'moveTo'를 '걸어서 이동'하는 내용으로 구현합니다.
     */
    override fun moveTo(dest: Point) {
        position = dest
        println("${name}이(가) 걸어서 ${dest}로 이동합니다.")
    }

    /**
     * Archer의 공격 (Attackable 구현) - 스크린샷 출력에 맞춤
     * [WHY] 'override'를 사용: 'attack' 기능을 '화살로 공격'하는 내용으로 구현합니다.
     * Archer는 'canAttackAir = true'이므로 공중/지상 유효성 검사가 필요 없습니다.
     */
    override fun attack(target: GameCharacter) {
        // Archer는 지상/공중 모두 공격 가능하므로 별도 검사 없음
        println("${name}가 ${target.name}를 ${attackType}로 공격합니다.")
    }
}

/**
 * Griffin (그리핀)
 * - GameCharacter을(를) 상속받는 공중 유닛 (isAirUnit = true)
 * - Attackable을(를) 구현 (공격 가능)
 * - canAttackAir = false (공중 공격 불가, 지상만 공격)
 *
 * [WHY] 'Griffin' 클래스를 만든 이유:
 * 'Griffin'은 'GameCharacter'를 직접 상속받습니다. ('GroundUnit'이 아님!)
 * 따라서 'Shuttle'에 탑승할 수 없습니다.
 */
class Griffin(name: String) : GameCharacter(name, true), Attackable {
    // Attackable 인터페이스 구현
    override val canAttackAir = false // 날아다니는 캐릭터(공중) 공격 불가
    override val attackType = "번개" // 스크린샷의 "번개를 내리칩니다"에 맞춤

    /** Griffin의 이동 방식 (Movable 구현) */
    override fun moveTo(dest: Point) {
        position = dest
        println("${name}이(가) 날아서 ${dest}로 이동합니다.")
    }

    /** Griffin의 공격 (Attackable 구현) - 스크린샷 출력에 맞춤 */
    override fun attack(target: GameCharacter) {
        // 1. 공중 유닛 공격 시도 여부 확인 (핵심 로직)
        if (target.isAirUnit && !canAttackAir) {
            println("${name}가 ${target.name}를 공격 할 수 없습니다. (공중 유닛)")
            return
        }
        // 2. 공격 실행 (출력)
        println("${name}가 ${target.name}에게 ${attackType}를 내리칩니다.")
    }
}

/**
 * Shuttle (셔틀)
 * - Transporter<GroundUnit>을(를) 상속받는 공중 유닛
 * - GroundUnit (Knight, Archer)만 최대 8기까지 태울 수 있음
 *
 * [WHY] 클래스 내부가 비어있는 이유 (상속과 제네릭의 장점):
 * 'Shuttle'은 'GroundUnit'을(를) 태우는 'Transporter'라고 선언하는 것만으로,
 * 부모인 'Transporter' 클래스에 이미 구현된 'board', 'unloadAll', 'moveTo' 기능을
 * 모두 자동으로 물려받습니다. (DRY 원칙)
 * 이것이 객체 지향의 상속과 제네릭을 통한 코드 재사용의 가장 큰 장점입니다.
 */
class Shuttle(name: String) : Transporter<GroundUnit>(name, position = Point(0, 0), capacity = 8) {
    // 별도 구현이 필요 없음. Transporter의 모든 기능을 상속받음.
}

// 6. 데모: 요구사항 시나리오 테스트

/**
 * main 함수: 프로그램의 시작점 (Entry Point) 입니다.
 *
 * [WHY] 'main' 함수를 작성하는 이유:
 * 위에서 설계하고 정의한 클래스(Knight, Archer 등)는 '설계도'일 뿐입니다.
 * 'main' 함수는 이 설계도를 바탕으로 실제 객체(인스턴스) (예: 'Knight1', 'Shuttle1')를 생성하고,
 * 'board', 'moveTo', 'attack' 등의 기능을 호출하여
 * 요구사항(과제 시나리오)에 맞게 프로그램이 동작하는지 검증하고 시연하는 역할을 합니다.
 */
fun main() {
    println("= Game Start =")
    // [WHY] List() 생성자 사용: 'Knight1', 'Knight2', ... 'Knight16' 객체를 반복문 없이
    // 한 줄의 코드로 효율적으로 생성하기 위해 'List(개수) { ... }' 문법을 사용합니다.
    val knights = List(16) { Knight("Knight${it + 1}") } // it는 0부터 15까지 증가
    val archers = List(16) { Archer("Archer${it + 1}") }
    val shuttles = List(4) { Shuttle("Shuttle${it + 1}") }
    val griffins = List(5) { Griffin("Griffin${it + 1}") }

    // [WHY] for 반복문 사용: 스크린샷의 탑승 순서와 요구사항(4대에 분배)을 맞추기 위해
    // 4대의 셔틀(shuttles[i])에 Knight 4기, Archer 4기씩(총 8기)을 태웁니다.
    for (i in 0 until 4) { // 4대의 셔틀 (i = 0, 1, 2, 3)
        val shuttle = shuttles[i]
        // println("--- ${shuttle.name} 탑승 시작 ---") // 스크린샷에는 이 라인이 없으므로 주석 처리
        for (j in 0 until 4) { // 4쌍 (Knight, Archer)
            // [WHY] 유닛 인덱스 계산:
            // i=0일 때 (0*4+j) -> 0, 1, 2, 3
            // i=1일 때 (1*4+j) -> 4, 5, 6, 7
            // ...
            val unitIndex = i * 4 + j
            shuttle.board(knights[unitIndex])
            shuttle.board(archers[unitIndex])
        }
    }

    // [WHY] forEach 사용: 셔틀 리스트(shuttles)와 그리핀 리스트(griffins)에 대해
    // 각 요소를 하나씩 꺼내어 'moveTo' 메서드를 호출합니다.
    val destination = Point(10, 10)
    shuttles.forEach { it.moveTo(destination) }
    griffins.forEach { it.moveTo(destination) }

    // [WHY] forEach 사용: 4대의 셔틀이 각각 'unloadAll' 메서드를 호출합니다.
    shuttles.forEach { it.unloadAll() }

    // [WHY] 테스트 유닛 선택: 공격 테스트를 위해
    // 각 유닛 리스트의 첫 번째(0번 인덱스) 유닛을 '공격자'로 선택합니다.
    val attackerKnight = knights[0]
    val attackerArcher = archers[0]
    val attackerGriffin = griffins[0]

    // [WHY] 공격 테스트: Knight의 'attack' 메서드가 '공중 공격 불가' 로직을
    // (canAttackAir = false) 잘 수행하는지 검증합니다.
    attackerKnight.attack(knights[1])      // vs Knight (지상) -> 성공
    attackerKnight.attack(archers[1])      // vs Archer (지상) -> 성공
    attackerKnight.attack(griffins[1])     // vs Griffin (공중) -> 실패
    attackerKnight.attack(shuttles[1])     // vs Shuttle (공중) -> 실패

    // [WHY] 공격 테스트: Archer의 'attack' 메서드가 '공중/지상 모두 공격' 로직을
    // (canAttackAir = true) 잘 수행하는지 검증합니다.
    attackerArcher.attack(archers[1])      // vs Archer (지상) -> 성공
    attackerArcher.attack(knights[1])      // vs Knight (지상) -> 성공
    attackerArcher.attack(griffins[1])     // vs Griffin (공중) -> 성공
    attackerArcher.attack(shuttles[1])     // vs Shuttle (공중) -> 성공

    // [WHY] 공격 테스트: Griffin의 'attack' 메서드가 '공중 공격 불가' 로직을
    // (canAttackAir = false) 잘 수행하는지 검증합니다.
    attackerGriffin.attack(griffins[1])    // vs Griffin (공중) -> 실패
    attackerGriffin.attack(archers[1])     // vs Archer (지상) -> 성공
    attackerGriffin.attack(knights[1])     // vs Knight (지상) -> 성공
    attackerGriffin.attack(shuttles[1])    // vs Shuttle (공중) -> 실패

}

