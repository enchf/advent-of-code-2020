import utils.fileLines
import utils.findGroups

/**
 * --- Day 7: Handy Haversacks ---
 *
 * You land at the regional airport in time for your next flight.
 * In fact, it looks like you'll even have time to grab some food:
 * all flights are currently delayed due to issues in luggage processing.
 *
 * Due to recent aviation regulations, many rules (your puzzle input)
 * are being enforced about bags and their contents;
 * bags must be color-coded and must contain specific quantities of other color-coded bags.
 * Apparently, nobody responsible for these regulations considered how long they would take to enforce!
 *
 * For example, consider the following rules:
 *
 * light red bags contain 1 bright white bag, 2 muted yellow bags.
 * dark orange bags contain 3 bright white bags, 4 muted yellow bags.
 * bright white bags contain 1 shiny gold bag.
 * muted yellow bags contain 2 shiny gold bags, 9 faded blue bags.
 * shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.
 * dark olive bags contain 3 faded blue bags, 4 dotted black bags.
 * vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.
 * faded blue bags contain no other bags.
 * dotted black bags contain no other bags.
 *
 * These rules specify the required contents for 9 bag types.
 * In this example, every faded blue bag is empty,
 * every vibrant plum bag contains 11 bags (5 faded blue and 6 dotted black), and so on.
 *
 * You have a shiny gold bag. If you wanted to carry it in at least one other bag,
 * how many different bag colors would be valid for the outermost bag?
 * (In other words: how many colors can, eventually, contain at least one shiny gold bag?)
 *
 * In the above rules, the following options would be available to you:
 *
 * - A bright white bag, which can hold your shiny gold bag directly.
 * - A muted yellow bag, which can hold your shiny gold bag directly, plus some other bags.
 * - A dark orange bag, which can hold bright white and muted yellow bags,
 *   either of which could then hold your shiny gold bag.
 * - A light red bag, which can hold bright white and muted yellow bags,
 *   either of which could then hold your shiny gold bag.
 *
 * So, in this example, the number of bag colors that can
 * eventually contain at least one shiny gold bag is 4.
 *
 * How many bag colors can eventually contain at least one shiny gold bag?
 * (The list of rules is quite long; make sure you get all of it.)
 *
 * --- Part Two ---
 *
 * It's getting pretty expensive to fly these days -
 * not because of ticket prices, but because of the ridiculous number of bags you need to buy!
 *
 * Consider again your shiny gold bag and the rules from the above example:
 *
 * faded blue bags contain 0 other bags.
 * dotted black bags contain 0 other bags.
 * vibrant plum bags contain 11 other bags: 5 faded blue bags and 6 dotted black bags.
 * dark olive bags contain 7 other bags: 3 faded blue bags and 4 dotted black bags.
 * So, a single shiny gold bag must contain 1 dark olive bag (and the 7 bags within it)
 * plus 2 vibrant plum bags (and the 11 bags within each of those): 1 + 1*7 + 2 + 2*11 = 32 bags!
 *
 * Of course, the actual rules have a small chance of going several levels deeper than this example;
 * be sure to count all of the bags, even if the nesting becomes topologically impractical!
 *
 * Here's another example:
 *
 * shiny gold bags contain 2 dark red bags.
 * dark red bags contain 2 dark orange bags.
 * dark orange bags contain 2 dark yellow bags.
 * dark yellow bags contain 2 dark green bags.
 * dark green bags contain 2 dark blue bags.
 * dark blue bags contain 2 dark violet bags.
 * dark violet bags contain no other bags.
 *
 * In this example, a single shiny gold bag must contain 126 other bags.
 *
 * How many individual bags are required inside your single shiny gold bag?
 *
 */
const val COLOR = "(.+) bags?"
const val CONTENT = "(no|[0-9]+) $COLOR"
const val PARSER = "$COLOR contain (.+)\\."

const val SHINY_GOLD = "shiny gold"

data class Content(val color: String, val amount: Int)
data class Rule(val color: String, val contents: List<Content>)
typealias Graph<T> = MutableMap<String, MutableSet<T>>

fun toSize(size: String) = if (size == "no") 0 else size.toInt()
fun <T> graph() = mutableMapOf<String, MutableSet<T>>()

fun String.toContents() = CONTENT
        .findGroups(this)
        .drop(1)
        .let { (size, color) -> Content(color, toSize(size)) }

fun String.toRule() = PARSER
        .findGroups(this)
        .let {
            Rule(
                it[1],
                it.last()
                  .split(", ")
                  .map(String::toContents)
                  .filter { content -> content.color != "other" }
            )
        }

fun <T> List<Rule>.toGraph(assigner: (Graph<T>, Rule, Content) -> Unit): Graph<T> = graph<T>()
        .also {
            forEach { rule ->
                it.putIfAbsent(rule.color, mutableSetOf())
                rule.contents.forEach { edge ->
                    it.putIfAbsent(edge.color, mutableSetOf())
                    assigner(it, rule, edge)
                }
            }
        }

fun allParents(rules: List<Rule>, color: String = SHINY_GOLD): Set<String> {
    val parents = mutableSetOf<String>()
    val parentStack = java.util.Stack<String>()
    val graph = rules.toGraph<String> { graph, rule, edge -> graph[edge.color]!!.add(rule.color) }

    parentStack.addAll(graph[color]!!)

    while (parentStack.isNotEmpty()) {
        val current = parentStack.pop()!!
        if (!parents.contains(current)) {
            parentStack.addAll(graph[current]!!)
            parents.add(current)
        }
    }

    return parents
}

fun costOf(graph: Graph<Pair<Int, String>>, vertex: String, memo: MutableMap<String, Int>): Int =
        graph[vertex]!!
                .also { println("Vertex: $vertex Edge $it Memo $memo") }
                .map { it.first + (it.first * costOf(graph, it.second, memo)) }
                .sum()
                .also { memo[vertex] = it }

fun bagSize(rules: List<Rule>, color: String = SHINY_GOLD) = rules
        .toGraph<Pair<Int, String>> { graph, rule, edge -> graph[rule.color]!!.add(Pair(edge.amount, edge.color)) }
        .let { costOf(it, color, mutableMapOf()) }

fun main() = fileLines("src/07_HandyHaversacks.txt", "src/07_Sample.txt", "src/07_Sample2.txt") { it.toRule() }
        .onEach { allParents(it).size.let(::println) } // Part 1
        .forEach { bagSize(it).let(::println) } // Part 2
