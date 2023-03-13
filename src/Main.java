import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws IOException {
        String folderPath = args[0];

        File folder = new File(folderPath);

        processFilesFromFolder(folder);
    }

    private static void processFilesFromFolder(File folder) throws IOException {
        File[] folderEntries = folder.listFiles();
        for (File entry : Objects.requireNonNull(folderEntries, "Folder must not be null")) {
            if (entry.isDirectory()) {
                processFilesFromFolder(entry);
                continue;
            }
            processImage(entry.getPath());
        }
    }

    private static void processImage(String imagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));

        int newCardCheckingWidth = Coordinates.NEW_CARD_CHECKING_WIDTH;
        int newCardCheckingHeight = Coordinates.NEW_CARD_CHECKING_HEIGHT;

        int newCardLeftUpperAngleX = Coordinates.LEFT_UPPER_CARD_POINT_X;
        int newCardLeftUpperAngleY = Coordinates.LEFT_UPPER_CARD_POINT_Y;

        String allCards = "";

        for (int i = 0; i < 5; i++) {
            Color nextCardColor = new Color(image.getRGB(newCardCheckingWidth, newCardCheckingHeight));

            if (isWhiteOrDarknessWhite(nextCardColor)) {
                newCardCheckingWidth += Coordinates.DIFFERENCE_BETWEEN_TWO_CARDS_IDENTICAL_AREAS;

                BufferedImage cardImage = image.getSubimage(
                        newCardLeftUpperAngleX,
                        newCardLeftUpperAngleY,
                        Coordinates.CARD_WIDTH,
                        Coordinates.CARD_HEIGHT);

                newCardLeftUpperAngleX += Coordinates.DIFFERENCE_BETWEEN_TWO_CARDS_IDENTICAL_AREAS;

                String value = findCardValue(cardImage);
                String suit = findCardSuit(cardImage);
                allCards += value + suit;

            } else break;
        }
        System.out.println(getImageName(imagePath) + " - " + allCards);
    }

    private static String findCardSuit(BufferedImage image) {
        if (isBlackOrDarknessBlack(new Color(image.getRGB(Coordinates.SUIT_CENTER_WIRTH, Coordinates.SUIT_CENTER_HEIGHT)))) {
            if (isBlackOrDarknessBlack(new Color(image.getRGB(Coordinates.SPADES_MARKER_WIDTH, Coordinates.SPADES_MARKER_HEIGHT)))) return "s";
            else return "c";
        } else {
            if (isWhiteOrDarknessWhite(new Color(image.getRGB(Coordinates.HEARTS_MARKER_WIDTH, Coordinates.HEARTS_MARKER_HEIGHT)))) return "h";
            else return "d";
        }
    }

    private static String findCardValue(BufferedImage image) {
        if (! isWhiteOrDarknessWhite(new Color(image.getRGB(Coordinates.CENTRAL_LOWER_X, Coordinates.CENTRAL_LOWER_Y)))) {
            if (! isWhiteOrDarknessWhite(new Color(image.getRGB(Coordinates.RIGHT_CENTRAL_X, Coordinates.RIGHT_CENTRAL_Y)))) {
                if (! isWhiteOrDarknessWhite(new Color(image.getRGB(Coordinates.ON_ZERO_X, Coordinates.ON_ZERO_Y)))) return "10";
                else return "Q";
            } else {
                if (! isWhiteOrDarknessWhite(new Color(image.getRGB(Coordinates.RIGHT_LOWER_ROUNDNESS_X, Coordinates.RIGHT_LOWER_ROUNDNESS_Y)))) {
                    if (! isWhiteOrDarknessWhite(new Color(image.getRGB(Coordinates.RIGHT_UPPER_X, Coordinates.RIGHT_UPPER_Y)))) {
                        if (! isWhiteOrDarknessWhite(new Color(image.getRGB(Coordinates.LEFT_UPPER_X, Coordinates.LEFT_UPPER_Y)))) {
                            if (! isWhiteOrDarknessWhite(new Color(image.getRGB(Coordinates.LEFT_LOWER_X, Coordinates.LEFT_LOWER_Y)))) return "8";
                            else return "9";
                        }
                        else return "J";
                    } else {
                        if (! isWhiteOrDarknessWhite(new Color(image.getRGB(Coordinates.LEFT_LOWER_X, Coordinates.LEFT_LOWER_Y)))) return "6";
                        else {
                            if (! isWhiteOrDarknessWhite(new Color(image.getRGB(Coordinates.LEFT_UPPER_X, Coordinates.LEFT_UPPER_Y)))) return "5";
                            else return "3";
                        }
                    }
                } else return "2";
            }
        } else {
            if (! isWhiteOrDarknessWhite(new Color(image.getRGB(Coordinates.RIGHT_LOWER_PEAK_X, Coordinates.RIGHT_LOWER_PEAK_Y)))) {
                if (! isWhiteOrDarknessWhite(new Color(image.getRGB(Coordinates.CENTRAL_UPPER_X, Coordinates.CENTRAL_UPPER_Y)))) return "A";
                else return "K";
            } else {
                if (! isWhiteOrDarknessWhite(new Color(image.getRGB(Coordinates.LEFT_UPPER_PEAK_X, Coordinates.LEFT_UPPER_PEAK_Y)))) return "7";
                else return "4";
            }
        }
    }

    private static boolean isWhiteOrDarknessWhite(Color color) {
        return color.equals(Colors.whiteShirtCard) || color.equals(Colors.darkenedShirtCard);
    }

    private static boolean isBlackOrDarknessBlack(Color color) {
        return color.equals(Colors.blackSuit) || color.equals(Colors.darkenedBlackSuit);
    }

    private static String getImageName(String imagePath) {
        Pattern pattern = Pattern.compile("\\w+\\.png\\b");
        Matcher matcher = pattern.matcher(imagePath);
        if (matcher.find()) {
            return matcher.group();
        } else throw new IllegalStateException("File name did not match with regular expression");
    }
}