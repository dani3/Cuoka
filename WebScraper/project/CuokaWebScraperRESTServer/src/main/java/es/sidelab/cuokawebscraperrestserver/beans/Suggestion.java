package es.sidelab.cuokawebscraperrestserver.beans;

import java.util.List;

/**
 * @class Clase que representa una lista de sugerencias.
 * @author Daniel Mancebo Aldea
 */

public class Suggestion 
{
    private List<String> suggestions;
    
    public Suggestion() {}
    
    public Suggestion( List<String> suggestions )
    {
        this.suggestions = suggestions;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }
}
