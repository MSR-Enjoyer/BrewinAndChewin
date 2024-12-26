package umpaz.brewinandchewin.client.recipebook;

public enum FermentingRecipeBookTab {
    MEALS("meals"),
    DRINKS("drinks");

    public final String name;

    FermentingRecipeBookTab(String name) {
        this.name = name;
    }

    public static FermentingRecipeBookTab findByName(String name) {
        for(FermentingRecipeBookTab value : values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }

        return null;
    }

    public String toString() {
        return this.name;
    }
}
