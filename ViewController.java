package advisor;

import java.util.ArrayList;
import java.util.List;

/*
nextPage - возвращает список элементов для следующей страницы или null, если уже показаны все элементы
prevPage - возвращает список элементов с предыдущей страницы, если такая существует или null
getNewReleases, getFeatured, getCategories, getPlaylist - вызывает соответствующий метод InfoProvider,
результат присваевается переменной currentArray, currentPage устанавливается на 1, а canChoicePage
становиться true, после чего возвращается часть списка соответсвующая первой странице
setCountOnPage - указывает кол-во элементов на странице (по умолчанию 5)
getInfoAboutPages - возвращает строку с инфой о страницах для вывода
*/

public class ViewController {

    private static int countOnPage = 5;

    private static ArrayList<?> currentArray;
    private static int currentPage = 0;
    private static int pagesCount = 0;
    private static boolean canChoicePage = false;

    public static List<?> getNewReleases(String access_token) {
        currentArray = InfoProvider.getNewReleases(access_token);
        currentPage = 1;
        pagesCount = currentArray.size() / countOnPage;
        if (currentArray.size() % countOnPage > 0) pagesCount++;
        canChoicePage = true;
        int itemCount = Math.min(countOnPage, currentArray.size());
        return currentArray.subList(0, itemCount);
    }

    public static List<?> getFeatured(String access_token) {
        currentArray = InfoProvider.getFeatured(access_token);
        currentPage = 1;
        pagesCount = currentArray.size() / countOnPage;
        if (currentArray.size() % countOnPage > 0) pagesCount++;
        canChoicePage = true;
        int itemCount = Math.min(countOnPage, currentArray.size());
        return currentArray.subList(0, itemCount);
    }

    public static List<?> getCategories(String access_token) {
        currentArray = InfoProvider.getCategories(access_token);
        currentPage = 1;
        pagesCount = currentArray.size() / countOnPage;
        if (currentArray.size() % countOnPage > 0) pagesCount++;
        canChoicePage = true;
        int itemCount = Math.min(countOnPage, currentArray.size());
        return currentArray.subList(0, itemCount);
    }

    public static List<?> getPlaylist(String categoryName, String access_token) {
        currentArray = InfoProvider.getPlaylist(categoryName, access_token);
        currentPage = 1;
        pagesCount = currentArray.size() / countOnPage;
        if (currentArray.size() % countOnPage > 0) pagesCount++;
        canChoicePage = true;
        int itemCount = Math.min(countOnPage, currentArray.size());
        return currentArray.subList(0, itemCount);
    }

    public static List<?> nextPage() {
        currentPage++;
        if (!canChoicePage || currentPage > pagesCount) {
            return null;
        }

        int itemCount;
        if (currentArray.size() % countOnPage * currentPage > 0) {
             itemCount = currentArray.size() % countOnPage + countOnPage * (currentPage - 1);
        } else itemCount = countOnPage * currentPage;

        return currentArray.subList(countOnPage * (currentPage - 1), itemCount);
    }

    public static List<?> prevPage() {
        currentPage--;
        if (!canChoicePage || currentPage <= 0) {
            return null;
        }

        return currentArray.subList(countOnPage * (currentPage - 1), countOnPage * currentPage);
    }

    public static void setCountOnPage(int countOnPage) {
        ViewController.countOnPage = countOnPage;
    }

    public static String getInfoAboutPages() {
        return "---PAGE " + currentPage + " OF " + pagesCount + "---";
    }
}
