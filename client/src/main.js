import * as Phaser from "phaser";
import GameScene from "./scenes/GameScene.js";

const config = {
    type: Phaser.AUTO,
    width: 1024,
    height: 1024,
    scene: [GameScene],
};

new Phaser.Game(config);
