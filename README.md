# DnD Character Manager

This is a desktop application for managing Dungeons & Dragons characters, built with Jetpack Compose for the UI and Kotlin.

## Features

*   **Character Inventory Management**: Create, manage, and view character inventories.
*   **Item Management**: Add, edit, and delete items in the inventory. Items can be categorized as weapons, consumables, potions, and miscellaneous.
*   **Spell Management**: Add, edit, and delete spells. Track spell slots and their usage.
*   **Data Persistence**: Character disk is saved locally in JSON format.

## Tech Stack

*   **UI**: Jetpack Compose for Desktop
*   **Language**: Kotlin and Java
*   **Data Serialization**: Jackson

## Project Structure

The project is organized into the following main components:

*   **`src/main/kotlin`**: Contains the Jetpack Compose UI code and disk models.
    *   `main` package: The main application code and core disk models for characters, inventories, and spells.
    *   `disk` package: Handles JSON serialization/deserialization and file operations.
    *   `main/ItemClasses` package: Defines the different types of items that can be in an inventory.

## How to Run

1.  Clone the repository.
2.  Open the project in IntelliJ IDEA.
3.  Run the `main` function in `src/main/kotlin/main/Main.kt`.
