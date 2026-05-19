# PIJON MOD
This mod adds **pigeons** with a twist to Minecraft 1.21.1!
## Basic Features
### 1. **Different Pijones variants:**
   
   The Pijones are not all the same... Depending on the biome, their colour changes!
   * **Grey, Brown, Brown-Grey, White and Dotted:** These are the most common types of pijones. You'll find them in almost every biome that a pijon can survive in.
     
   * **Red:** Spawns only in the _Mushroom Fields_.
     
   * **Purple:** Only spawns in Cherry Groves and has a chance of 1 in 100 to spawn!

### 2. **Taming:**

The wild Pijones are deathly afraid of the players. If you try to approach them suddenly, they will fly away.

* **How to approach them:** If you are holding seeds that a villager can plant, all the Pijones in the near area will gather around you. Also, if no seeds are available, by holding **Shift** and sneaking up to them, the pijones will sit calmly and let you approach them.

* **How to tame them:** By feeding a Pijon seeds, just like other tamable animals, the pijon will eventually trust you.

* **Healing and feeding:** If a Pijon is tamed by the player, it has the ability to be fed. If it's full, green stars will appear above it. If it's health isn't full, red hearts will appear above it.

* **Sitting:** With a simple **Right click** the Pijon you have tamed will sit down and stop following you arround.

### 3. **Letter delivering:**
When in multiplayer, the Pijones are able to deliver letters from player to player!
* Aquire a **Book and Quill** and inside write your desired letter.
  
* **Sign** the book with the name of the player the pijon has to find, or name a paper on an anvil with their name.
  
* **Right Click** your tamed Pijon.
  
* The Pijon will run away from you and find the recipient of the letter. Once the Pijon finds the player, it'll throw the letter on the ground in front of them and you'll receive a confirmation message in chat, saying `"Letter delivered!"`.

### 4. **New Items and Receipes:**
The addition of the Pijones also brings new items:
* **Feather Variants:** When a Pijon dies, depending on its colour, it drops a different coloured feather. This acts just like a chicken's feather and allows you to craft book and quill, arrows and brushes.

* **Pijon Poops:** Just like in real life, the Pijones will randomly drop poops all around the world! This is dropped as an item that the player can pick up. It can be thrown to other players, dealing damage to them and attaching them the **Stink effect**! This effect stays on the player for ever, unless they 'bathe' by touching water.

* **Stink potions and tipped arrows:** When you've acquired even a singular Pijon poop, you can brew it into a **Stink Potion** using **Awkward Potions** as a base. This attaches to the player the **Stink Effect**, just like the Pijon poop, and also grants them the **Poison Effect** for a few seconds.

### 5. **Defending their owner:**
Just like the wolves, when a Pijon is tamed by a player, they will follow them everywhere and stay loyal to them. If the owner of a Pijon is attacked by something or attacks something, the Pijon will fly above the target and bombard them with poops, dealing damage!

### 6. **Custom sound effects:**
Sounds from the pigeons that we are all used to in real life are implemented to the game!

### 7. **Easter Egg:**
Play around with classic Nametag easter eggs and discover another one...

## Instructions for Installing the Mod

### Requirements:
* **Minecraft Version:** 1.21
* **Mod Loader:** Forge for 1.21
* **Java Version:** Java Development Kit (JDK) 21

### Installation (For Players):

1. Download and install the [Minecraft Launcher](https://www.minecraft.net/en-us/download) if you don't have it.
2. Download and install **Forge** for Minecraft 1.21.
3. Download the latest `PijonMod.jar` file.
4. Press `Win + R`, type `%appdata%\.minecraft` and press Enter to open your Minecraft directory.
5. Place the downloaded `.jar` file into the `mods` folder (create the folder if it doesn't exist).
6. Open the Minecraft Launcher, select the Forge profile, and click Play!

### Installation (For Developers):
Follow these steps if you want to view or edit the source code:

1. **Clone the repository:** Download or clone this repo to your local machine.
2. **Open the IDE:** Open the project folder using **IntelliJ IDEA** (recommended) or VS Code.
3. **Setup Java:** Ensure you have JDK 21 installed and set as the Project SDK in your IDE.
4. **Build the project:** Open the terminal in your IDE and run the following command to build the mod:
   * Windows: `gradlew build`
   * Mac/Linux: `./gradlew build`
5. **Run the game:** To launch the Minecraft client directly from the code, run:
   * Windows: `gradlew runClient`
   * Mac/Linux: `./gradlew runClient`
