MORD ORDERS 1.0.0 - SOURCE PROJECT

Features:
- /orders opens a configurable server order GUI.
- Clicking an order submits the required material from the player's inventory and pays Vault economy money.
- /shards shows a separate saved Shards balance.
- /shards give|take|set <online-player> <amount> manages Shards (operator permission).
- Shards are saved in plugins/MordOrders/shards.yml.

Requirements:
- Paper 1.21.11
- Java 21
- Vault plus an economy provider (for order payouts)

Build:
1. Open this folder in IntelliJ IDEA as a Maven project.
2. Wait for Maven to download Paper API and VaultAPI.
3. Run Maven goal: clean package
4. The plugin JAR will be in target/MordOrders-1.0.0.jar
5. Put that JAR in your server's plugins folder and restart.

Configure order materials, amounts, and payouts in plugins/MordOrders/config.yml after first startup.

Note: This is a starter implementation. Orders are fixed/configured server orders; player-created marketplace orders, refresh timers, and a Shards shop are not included yet.
