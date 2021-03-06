package com.arit.adserve.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name="item")
@ToString
public class Item {

	@Id
	private String id;
	@NotBlank(message = "providerItemId is mandatory")
	private String providerItemId;
	@NotBlank(message = "providerName is mandatory")
	private String providerName;
	private Integer relevance;
	@NotBlank(message = "title is mandatory")
	private String title;
	@NotBlank(message = "viewItemURL is mandatory")
	@Size(min = 15)
	private String viewItemURL;
	@ElementCollection
	private Set<String> subTitles = new HashSet<>();
	private String description;
	private String location;
	private String country;
	private String condition;
	private String currency;
	private int price;  //the price in cents, pence, kopecks etc.
	private String source;
	private String productId;
	private String galleryURL;
	private String priceFormatted;
	private String image64BaseStr;
	private String modifiedImage64BaseStr;
	private Date modifiedImageDate;
	private boolean process;
	private boolean deleted;
	private int rank;
	private Date createdOn = new Date();
	private Date updatedOn = new Date();

	/**
	 * @return lucene document for indexing
	 */
	@Transient
	@JsonIgnore
	public Document getLuceneDocument() {
		Document document = new Document();
		document.add(new StoredField("id", this.id));
		document.add(new StringField("providerName", this.providerName, Field.Store.YES));
		if(StringUtils.isNotBlank(this.country)) document.add(new StringField("country", this.country, Field.Store.YES));
		if(StringUtils.isNotBlank(this.title)) document.add(new TextField("title", this.title, Field.Store.YES));
		if(!this.subTitles.isEmpty()) document.add(new TextField("subTitles", this.subTitles.stream().collect(Collectors.joining(" ")), Field.Store.NO));
		if(StringUtils.isNotBlank(this.description))document.add(new TextField("description", this.description, Field.Store.YES));
		if(price>0) { document.add(new IntPoint("price", this.price)); }
		document.add(new SortedNumericDocValuesField("rank", this.rank));
		return document;
	}


}
