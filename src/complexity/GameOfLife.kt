package complexity

data class GameOfLife<T>(val matrix: List<List<T>>, val evolution: (GameOfLife<T>, T, Int, Int) -> T)

fun <T> GameOfLife<T>.evolve() = GameOfLife(
    matrix.withIndex().map { (i, row) -> row.withIndex().map { (j, cell) -> evolution(this, cell, i, j) } },
    evolution
)

fun <T> GameOfLife<T>.stabilize() = generateSequence(this) { it.evolve() }
    .zipWithNext()
    .find { (before, after) -> before.matrix == after.matrix }
    ?.second ?: error("Infinite loop")

// Rewind cursor not working in IntelliJ console.
fun <T> GameOfLife<T>.rewindCursor() = repeat(matrix.size) { print("\u001B[1A") }
fun <T> GameOfLife<T>.print() = matrix.forEach { row -> row.forEach { print(it) }.also { println() } }

fun <T> GameOfLife<T>.play() = generateSequence(this) { it.evolve() }
    .forEach { it.run { print().also { rewindCursor() }.also { Thread.sleep(666) } } }

fun <T> GameOfLife<T>.validX(x: Int) = x >= 0 && x < matrix.size
fun <T> GameOfLife<T>.validY(y: Int) = y >= 0 && y < matrix.first().size
fun <T> GameOfLife<T>.validCoordinates(x: Int, y: Int) = validX(x) && validY(y)

fun <T> GameOfLife<T>.neighbours(x: Int, y: Int) =
    (-1 .. 1).flatMap { i -> (-1 .. 1).map { j -> Pair(i + x, j + y) } }
        .filterNot { (i, j) -> i == x && j == y }
        .filter { (i, j) -> validCoordinates(i, j) }
        .map { (i, j) -> matrix[i][j] }
