import * as Phaser from "phaser";
const TILE_SIZE = 32;

export default class GameScene extends Phaser.Scene {
    constructor() {
        super("GameScene");
    }

    // TODO rename the assets
    preload() {
        this.load.tilemapTiledJSON("map", "/assets/test.tmj");
        this.load.image("tileset", "/assets/mountain_landscape.png");
    }

    create() {
        const map = this.make.tilemap({ key: "map" });
        const tiles = map.addTilesetImage("idk", "tileset");
        this.layer = map.createLayer("Tile Layer 1", tiles, 0, 0);

        this.currentTileX = 3;
        this.currentTileY = 2;
        this.player = this.add.rectangle(
            this.currentTileX * TILE_SIZE,
            this.currentTileY * TILE_SIZE,
            TILE_SIZE,
            TILE_SIZE,
            0x000000,
        );
        this.player.setOrigin(0, 0);

        this.cameras.main.setBounds(
            0,
            0,
            map.widthInPixels,
            map.heightInPixels,
        );
        this.cameras.main.startFollow(this.player, true);

        this.socket = new WebSocket("ws://localhost:8080");

        this.socket.onopen = () => {
            console.log("Connected to server");
        };

        this.socket.onmessage = (event) => {
            const msg = JSON.parse(event.data);
            if (msg.type === "path") {
                this.followPath(msg.tiles);
            }
        };

        this.socket.onclose = () => {
            console.log("Disconnected from server");
        };

        this.lastMoveSent = 0;
        const TICK_MS = 600;

        this.input.on("pointerdown", (pointer) => {
            const now = Date.now();
            if (now - this.lastMoveSent < TICK_MS) return;

            const world = this.cameras.main.getWorldPoint(pointer.x, pointer.y);
            const tile = this.layer.getTileAtWorldXY(world.x, world.y);
            if (tile && this.socket.readyState === WebSocket.OPEN) {
                this.lastMoveSent = now;
                this.socket.send(
                    JSON.stringify({
                        type: "move",
                        x: tile.x,
                        y: tile.y,
                        fromX: this.currentTileX,
                        fromY: this.currentTileY,
                    }),
                );
            }
        });
    }

    followPath(tiles) {
        if (!tiles || tiles.length === 0) return;

        this.tweens.killTweensOf(this.player);

        this.moveGeneration = (this.moveGeneration || 0) + 1;
        const myGeneration = this.moveGeneration;

        let step = 0;

        const moveNextStep = () => {
            if (this.moveGeneration !== myGeneration) return;
            if (step >= tiles.length) {
                this.isMoving = false;
                return;
            }

            const tile = tiles[step];
            this.currentTileX = tile.x;
            this.currentTileY = tile.y;
            step++;

            this.tweens.add({
                targets: this.player,
                x: tile.x * TILE_SIZE,
                y: tile.y * TILE_SIZE,
                duration: 150,
                ease: "Linear",
                onComplete: moveNextStep,
            });
        };

        this.isMoving = true;
        moveNextStep();
    }
}
