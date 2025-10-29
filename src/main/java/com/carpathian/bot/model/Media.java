package com.carpathian.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a photo or video attached to a house.  Media items can
 * store both the original URL and the Telegram file_id to allow
 * re‑sending media without consuming bandwidth【344534168974895†L1037-L1041】.  The
 * owning house is linked via a many‑to‑one association.
 */
@Entity
@Table(name = "media")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** HTTP or storage service URL pointing to the media resource. */
    private String url;

    /** Telegram file identifier returned upon upload; used to avoid re‑uploading. */
    private String fileId;

    @Enumerated(EnumType.STRING)
    private MediaType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id")
    private House house;
}