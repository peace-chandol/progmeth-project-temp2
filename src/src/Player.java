package src;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

public class Player {
//    private Node player;
    private boolean canJump = true;
    private HashMap<KeyCode, Boolean> keys;
    private int levelWidth;
	private ArrayList<Node> platforms = new ArrayList<>();
	private Point2D playerVelocity = new Point2D(0, 0);
	private ImageView player =  new ImageView(new Image(getClass().getResourceAsStream("/res/player_currentRight.png")));
    
	public Player(Pane gameRoot, HashMap<KeyCode, Boolean> keys) {
        this.keys = keys;
        levelWidth = LevelData.LEVEL1[0].length() * 60;
        player = new ImageView(new Image(getClass().getResourceAsStream("/res/player_currentRight.png")));
        player.setFitWidth(40);
        player.setFitHeight(40);
        player.setTranslateX(0);
        player.setTranslateY(600);
        player.translateXProperty().addListener((obs, old, newValue) -> {
            int offset = newValue.intValue();

            if (offset > 640 && offset < levelWidth - 640) {
                gameRoot.setLayoutX(-(offset - 640));
            }
        });

        for (int i = 0; i < LevelData.LEVEL1.length; i++) {
            String line = LevelData.LEVEL1[i];
            for (int j = 0; j < line.length(); j++) {
                switch (line.charAt(j)) {
                    case '0':
                        break;
                    case '1':
                        Node platform = createEntity(j * 60, i * 60, 60, 60, Color.BROWN);
                        platforms.add(platform);
                        break;
                }
            }
        }
        
        gameRoot.getChildren().add(player);
        gameRoot.getChildren().addAll(platforms);
    }

    public void update() {
        if (isPressed(KeyCode.W) && player.getTranslateY() >= 5) {
            jumpPlayer();
        }

        if (isPressed(KeyCode.A) && player.getTranslateX() >= 5) {
            movePlayerX(-5);
        }

        if (isPressed(KeyCode.D) && player.getTranslateX() + 40 <= levelWidth - 5) {
            movePlayerX(5);
        }

        if (playerVelocity.getY() < 10) {
            playerVelocity = playerVelocity.add(0, 1);
        }

        movePlayerY((int)playerVelocity.getY());
        

    }

    private void movePlayerX(int value) {
        boolean movingRight = value > 0;

        for (int i = 0; i < Math.abs(value); i++) {
            for (Node platform : platforms) {
                if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                    if (movingRight) {
                        if (player.getTranslateX() + 40 == platform.getTranslateX()) {
                            return;
                        }
                    }
                    else {
                        if (player.getTranslateX() == platform.getTranslateX() + 60) {
                            return;
                        }
                    }
                }
            }
            player.setTranslateX(player.getTranslateX() + (movingRight ? 1 : -1));
        }
    }

    private void movePlayerY(int value) {
        boolean movingDown = value > 0;

        for (int i = 0; i < Math.abs(value); i++) {
            for (Node platform : platforms) {
                if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                    if (movingDown) {
                        if (player.getTranslateY() + 40 == platform.getTranslateY()) {
                            player.setTranslateY(player.getTranslateY() - 1);
                            canJump = true;
                            return;
                        }
                    }
                    else {
                        if (player.getTranslateY() == platform.getTranslateY() + 60) {
                            return;
                        }
                    }
                }
            }
            player.setTranslateY(player.getTranslateY() + (movingDown ? 1 : -1));
        }
    }

    private void jumpPlayer() {
        if (canJump) {
            playerVelocity = playerVelocity.add(0, -30);
            canJump = false;
        }
    }

    private Node createEntity(double x, double y, double w, double h, Color color) {
		Rectangle entity = new Rectangle(w, h);
        entity.setTranslateX(x);
        entity.setTranslateY(y);
        entity.setFill(color);
        entity.getProperties().put("alive", true);

//        gameRoot.getChildren().add(entity);
        return entity;
	}

    private boolean isPressed(KeyCode key) {
        return keys.getOrDefault(key, false);
    }
}
