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
