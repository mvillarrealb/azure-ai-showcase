package org.mavb.azure.ai.entity;

import com.azure.search.documents.indexes.SearchableField;
import com.azure.search.documents.indexes.SimpleField;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankDocument {
    @SimpleField(isKey = true)
    private String id;

    @SearchableField
    private String name;
    
    @SearchableField
    private String description;

    @SearchableField(vectorSearchDimensions = 1536, vectorSearchProfileName = "vector-config")
    private List<Float> embedding;
}