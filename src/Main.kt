// Concept Note: https://docs.google.com/document/d/1ZZTqnIoSN8izuZRGja6cyKFHfRWaWbk-K_xyNgmKJnA/edit?tab=t.0
// Group 7 - Penina Otieno , 170966
//         - James Gachago , 170398
//         - David Agonga , 145937
//         - Michelle Miako , 177877
//         - Lesley Mwangi , 171762



data class Transaction(val description: String, val amount: Double)
data class Product(val id: Int, var name: String, var quantity: Int)

fun main() {

    // ------------------------------------------------------
    // BUSINESS INFORMATION
    // ------------------------------------------------------
    val businessId: Int = 1001
    val businessName: String = "BizTrack Demo Kiosk"

    // ------------------------------------------------------
    // SALES AND EXPENSES (Collections of Transaction)
    // ------------------------------------------------------
    val sales = mutableListOf<Transaction>()
    val expenses = mutableListOf<Transaction>()

    // ------------------------------------------------------
    // INVENTORY
    // ------------------------------------------------------
    var nextProductId = 1
    val inventory = mutableListOf<Product>()
    inventory.add(Product(nextProductId++, "Bread", 20))
    inventory.add(Product(nextProductId++, "Milk", 15))
    inventory.add(Product(nextProductId++, "Sugar", 30))

    // ------------------------------------------------------
    // BUDGET
    // ------------------------------------------------------
    var monthlyBudget: Double = 30000.0

    var running: Boolean = true

    println()
    println("========================================")
    println("              B I Z T R A C K")
    println("  Daily Financial Management Application")
    println("========================================")
    println("Business ID: $businessId")
    println("Business: $businessName")
    println()

    while (running) {

        println()
        println("----------------------------------------")
        println("                MAIN MENU")
        println("----------------------------------------")
        println("1. Record daily sale")
        println("2. Record business expense")
        println("3. View profit / loss status")
        println("4. Track inventory")
        println("5. Create and monitor budget")
        println("6. Search inventory")
        println("7. Financial literacy tips")
        println("8. Exit")
        println("----------------------------------------")
        print("Choose an option: ")

        val choice: String = readln().trim()

        when (choice) {

            // ------------------------------------------
            // 1. RECORD DAILY SALE
            // ------------------------------------------
            "1" -> {
                println()
                println("----------- RECORD SALE -----------")
                print("What did you sell? ")
                val item = readln().trim()

                print("Enter sale amount (KES): ")
                val amount: Double? = readln().trim().toDoubleOrNull()

                if (amount != null && amount > 0) {
                    sales.add(Transaction(item, amount))
                    println("Sale recorded: $item - KES ${"%.2f".format(amount)}")
                } else {
                    println("Invalid amount. Sale amount must be greater than zero.")
                }
            }

            // ------------------------------------------
            // 2. RECORD BUSINESS EXPENSE
            // ------------------------------------------
            "2" -> {
                println()
                println("----------- RECORD EXPENSE -----------")
                print("What was the expense for? ")
                val item = readln().trim()

                print("Enter expense amount (KES): ")
                val amount: Double? = readln().trim().toDoubleOrNull()

                if (amount != null && amount > 0) {
                    expenses.add(Transaction(item, amount))
                    println("Expense recorded: $item - KES ${"%.2f".format(amount)}")
                } else {
                    println("Invalid amount. Expense amount must be greater than zero.")
                }
            }

            // ------------------------------------------
            // 3. CALCULATE PROFIT OR LOSS
            // ------------------------------------------
            "3" -> {
                println()
                println("----------- PROFIT / LOSS STATUS -----------")

                val totalSales = sales.sumOf { it.amount }
                val totalExpenses = expenses.sumOf { it.amount }
                val profit = totalSales - totalExpenses

                println("Total Sales:     KES ${"%.2f".format(totalSales)}")
                println("Total Expenses:  KES ${"%.2f".format(totalExpenses)}")
                println("Net Result:      KES ${"%.2f".format(profit)}")
                println()

                // if / else if / else, as specified in the concept note
                if (profit > 0) {
                    println("Status: Your business is making a PROFIT.")
                } else if (profit == 0.0) {
                    println("Status: Your business is BREAKING EVEN.")
                } else {
                    println("Status: Your business is making a LOSS.")
                }
            }

            // ------------------------------------------
            // 4. TRACK INVENTORY
            // ------------------------------------------
            "4" -> {
                println()
                println("----------- INVENTORY -----------")

                if (inventory.isEmpty()) {
                    println("No products in inventory yet.")
                } else {
                    for (product in inventory) {
                        println("${product.id}. ${product.name} - Quantity: ${product.quantity}")
                    }
                }

                println()
                println("1. Add new product")
                println("2. Remove a product")
                println("3. Return to main menu")
                print("Choose an option: ")

                val inventoryChoice = readln().trim()

                if (inventoryChoice == "1") {
                    print("Enter product name: ")
                    val newName = readln().trim()

                    print("Enter starting quantity: ")
                    val newQuantity: Int? = readln().trim().toIntOrNull()

                    if (newQuantity != null && newQuantity >= 0) {
                        inventory.add(Product(nextProductId++, newName, newQuantity))
                        println("$newName added to inventory with quantity $newQuantity.")
                    } else {
                        println("Invalid quantity. Product not added.")
                    }

                } else if (inventoryChoice == "2") {
                    print("Enter the name of the product to remove: ")
                    val removeName = readln().trim()

                    var found: Boolean = false
                    val iterator = inventory.iterator()
                    while (iterator.hasNext()) {
                        val product = iterator.next()
                        if (product.name.equals(removeName, ignoreCase = true)) {
                            iterator.remove()
                            found = true
                            println("$removeName has been removed from inventory.")
                            break
                        }
                    }

                    if (!found) {
                        println("Product not found in inventory.")
                    }

                } else {
                    println("Returning to main menu...")
                }
            }

            // ------------------------------------------
            // 5. CREATE AND MONITOR BUDGET
            // ------------------------------------------
            "5" -> {
                println()
                println("----------- BUDGET MANAGEMENT -----------")

                val totalExpenses = expenses.sumOf { it.amount }
                val remainingBudget = monthlyBudget - totalExpenses

                println("Monthly Budget:    KES ${"%.2f".format(monthlyBudget)}")
                println("Amount Spent:      KES ${"%.2f".format(totalExpenses)}")
                println("Remaining Budget:  KES ${"%.2f".format(remainingBudget)}")
                println()

                if (remainingBudget > 0) {
                    println("Status: You are within your budget.")
                } else if (remainingBudget == 0.0) {
                    println("Status: Your budget has been fully used.")
                } else {
                    println("Warning: You have exceeded your budget!")
                }

                println()
                println("Do you want to set a new monthly budget? (yes/no)")
                val changeBudget = readln().trim().lowercase()

                if (changeBudget == "yes") {
                    print("Enter new monthly budget (KES): ")
                    val newBudget: Double? = readln().trim().toDoubleOrNull()

                    if (newBudget != null && newBudget > 0) {
                        monthlyBudget = newBudget
                        println("Budget updated successfully!")
                    } else {
                        println("Invalid amount. Budget was not changed.")
                    }
                }
            }

            // ------------------------------------------
            // 6. SEARCH INVENTORY
            // ------------------------------------------
            "6" -> {
                println()
                println("----------- SEARCH INVENTORY -----------")
                print("Enter product name to search: ")
                val searchName = readln().trim()

                var found: Boolean = false

                for (product in inventory) {
                    if (product.name.equals(searchName, ignoreCase = true)) {
                        println("Product found: ${product.name}")
                        println("Quantity available: ${product.quantity}")
                        found = true
                    }
                }

                if (!found) {
                    println("Product not found in inventory.")
                }
            }

            // ------------------------------------------
            // 7. FINANCIAL LITERACY TIPS
            // (when expression, as specified in the concept note)
            // ------------------------------------------
            "7" -> {
                println()
                println("------- FINANCIAL LITERACY -------")
                println("1. Revenue")
                println("2. Profit")
                println("3. Expenses")
                println("4. Capital")
                println("5. Cash Flow")
                print("Choose a topic: ")

                val topic = readln().trim()

                when (topic) {
                    "1" -> {
                        println()
                        println("REVENUE")
                        println("Revenue is the total money earned by a business")
                        println("from selling goods or services, before expenses.")
                    }
                    "2" -> {
                        println()
                        println("PROFIT")
                        println("Profit is the money remaining after business")
                        println("expenses are deducted from revenue.")
                        println("Formula: Profit = Revenue - Expenses")
                    }
                    "3" -> {
                        println()
                        println("EXPENSES")
                        println("Expenses are costs incurred while running a")
                        println("business, e.g. rent, transport, and restocking.")
                    }
                    "4" -> {
                        println()
                        println("CAPITAL")
                        println("Capital is the money or resources used to")
                        println("start or operate a business.")
                    }
                    "5" -> {
                        println()
                        println("CASH FLOW")
                        println("Cash flow is money moving into and out of a")
                        println("business. Positive cash flow means more money")
                        println("is coming in than going out.")
                    }
                    else -> {
                        println("Invalid topic selected.")
                    }
                }
            }

            // ------------------------------------------
            // 8. EXIT
            // ------------------------------------------
            "8" -> {
                println()
                println("Are you sure you want to exit? (yes/no)")

                var confirmation: String

                do {
                    print("Enter yes or no: ")
                    confirmation = readln().trim().lowercase()

                    if (confirmation != "yes" && confirmation != "no") {
                        println("Invalid response.")
                    }
                } while (confirmation != "yes" && confirmation != "no")

                if (confirmation == "yes") {
                    running = false
                    println()
                    println("Thank you for using BizTrack!")
                } else {
                    println("Returning to main menu...")
                }
            }

            // ------------------------------------------
            // INVALID OPTION
            // ------------------------------------------
            else -> {
                println()
                println("Invalid option. Please choose a number from 1 to 8.")
            }
        }
    }
}
