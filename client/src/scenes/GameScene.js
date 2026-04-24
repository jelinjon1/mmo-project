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

        this.player = this.add.rectangle(
            3 * TILE_SIZE,
            2 * TILE_SIZE,
            TILE_SIZE,
            TILE_SIZE,
            0x000000,
        );
        this.player.setOrigin(0, 0);

        this.input.on("pointerdown", (pointer) => {
            const tile = this.layer.getTileAtWorldXY(pointer.x, pointer.y);
            if (tile) {
                this.player.setPosition(tile.x * TILE_SIZE, tile.y * TILE_SIZE);
            }
        });
    }
}
