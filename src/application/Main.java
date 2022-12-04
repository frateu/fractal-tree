package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		final Double baseSize = 100.0;
		final Integer maxLevel = 10;

		Double baseX = 200.0;
		Double baseY = 250.0;
		Integer level = 0;
		Double angle = 0.5;

		ArrayList<List<Double>> allBranches = new ArrayList<>();

		try {
			Group root = new Group();

			Canvas canvas = new Canvas(400, 400);

			GraphicsContext gc = canvas.getGraphicsContext2D();
			gc.setStroke(Color.GREEN);

			// Construir a base da arvore
			drawLine(gc, 200, 350, baseX, baseY);

			allBranches = buildBranches(baseX, baseY, baseSize, allBranches, level, maxLevel, angle);

			final ArrayList<List<Double>> innerAllBranches = new ArrayList<>(allBranches);

			AtomicInteger counter = new AtomicInteger();
			Timeline timeleine = new Timeline(new KeyFrame(Duration.millis(10), (ActionEvent event) -> {
				List<Double> tempCoors = innerAllBranches.get(counter.getAndIncrement());
				drawLine(gc, tempCoors.get(0), tempCoors.get(1), tempCoors.get(2), tempCoors.get(3));
			}));
			timeleine.setCycleCount(allBranches.size());
			timeleine.play();

			root.getChildren().add(canvas);

			primaryStage.setScene(new Scene(root, Color.BLACK));
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void drawLine(GraphicsContext gc, double x1, double y1, double x2, double y2) {
		Affine prije = gc.getTransform();

		Double dx = x2 - x1, dy = y2 - y1;
		Double angle = Math.atan2(dy, dx);

		int len = (int) Math.sqrt(dx * dx + dy * dy);

		Transform transform = Transform.translate(x1, y1);

		transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));

		gc.setTransform(new Affine(transform));
		gc.strokeLine(0, 0, len, 0);
		gc.setTransform(prije);
	}

	private ArrayList<List<Double>> buildBranches(Double baseX, Double baseY, Double baseSize,
			ArrayList<List<Double>> allBranches, Integer level, Integer maxLevel, Double angle) {
		Double actualBaseSize = baseSize * 0.67;

		level++;

		Double newX = Math.sin(angle) * actualBaseSize;
		Double newY = Math.cos(angle) * actualBaseSize;

		Double nextX = baseX + newX;
		Double nextY = baseY - newY;

		List<Double> newBranch = new ArrayList(Arrays.asList(baseX, baseY, nextX, nextY));

		allBranches.add(newBranch);

		angle = angle + 0.7;

		if (level < maxLevel) {

			allBranches = buildBranches(nextX, nextY, actualBaseSize, allBranches, level, maxLevel, angle);
		}

		// Outro lado da branch

		angle = angle + (0.7 * 2);

		newX = Math.sin(angle) * actualBaseSize;
		newY = Math.cos(angle) * actualBaseSize;

		actualBaseSize = baseSize / 0.67;

		nextX = baseX - newX;
		nextY = baseY + newY;

		newBranch = new ArrayList(Arrays.asList(baseX, baseY, nextX, nextY));

		allBranches.add(newBranch);

		angle = angle - (0.7 * 4);

		actualBaseSize = baseSize * 0.67;

		if (level < maxLevel) {
			allBranches = buildBranches(nextX, nextY, actualBaseSize, allBranches, level, maxLevel, angle);
		}

		level--;

		return allBranches;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
