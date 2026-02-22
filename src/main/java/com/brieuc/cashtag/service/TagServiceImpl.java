package com.brieuc.cashtag.service;

import com.brieuc.cashtag.entity.Tag;
import com.brieuc.cashtag.exception.EntityNotFoundException;
import com.brieuc.cashtag.repository.TagRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;
import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

@Service
@Validated
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Value("${app.uploads.path}")
    private String uploadsPath;

    private static final Set<String> ALLOWED_TYPES = Set.of("image/png", "image/jpeg", "image/webp", "image/svg+xml");
    private static final long MAX_SIZE = 500 * 1024; // 500 Ko
    private static final int ICON_SIZE = 128;

    @Override
    public Page<Tag> getTags(@NotNull Specification<Tag> specification, @NotNull Pageable pageable) {
        return tagRepository.findAll(specification, pageable);
    }

    @Override
    public Tag getById(@NotNull Long id) {
        return tagRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No tag id " + id + " was found"));
    }

    @Override
    public Tag create(@NotNull Tag tag) {
        //TODO Put some validation on sortingOrder when creating or updating
        tag.setSortingOrder((int) (tagRepository.count()+1));
        return tagRepository.save(tag);
    }

    @Override
    public void deleteById(@NotNull Long id) {
        tagRepository.deleteById(id);
    }

    @Override
    public Tag update(@NotNull Tag detachedTag) {
        Tag tag = getById(detachedTag.getId());
        tag.setTitle(detachedTag.getTitle());
        tag.setDescription(detachedTag.getDescription());
        tag.setIcon(detachedTag.getIcon());
        tag.setSortingOrder(detachedTag.getSortingOrder());
        tag.setIsCumulative(detachedTag.getIsCumulative());
        tag.setHidden(detachedTag.getHidden());
        tag.setCurrency(detachedTag.getCurrency());
        return tagRepository.save(tag);
    }

    @Override
    public Tag uploadIcon(Long tagId, MultipartFile file) {
        // Validation
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Empty File");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("File too big (max 500 Ko)");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Unauthorized Type");
        }

        Tag tag = getById(tagId);

        boolean isSvg = "image/svg+xml".equals(file.getContentType());
        String filename = UUID.randomUUID() + (isSvg ? ".svg" : ".png");
        Path targetPath = Paths.get(uploadsPath, "tags", filename);

        try {
            Files.createDirectories(targetPath.getParent());
            if (isSvg) {
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                resizeImage(file, targetPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error when uploading", e);
        }

        // Delete former icon file
        if (tag.getIcon() != null) {
            try {
                Files.deleteIfExists(Paths.get(uploadsPath, tag.getIcon()));
            } catch (IOException ignored) {}
        }

        tag.setIcon("tags/" + filename);
        return tagRepository.save(tag);
    }

    private void resizeImage(MultipartFile file, Path targetPath) throws IOException {
        BufferedImage original = ImageIO.read(file.getInputStream());

        int width = original.getWidth();
        int height = original.getHeight();
        double ratio = Math.min((double) ICON_SIZE / width, (double) ICON_SIZE / height);
        int newWidth = (int) (width * ratio);
        int newHeight = (int) (height * ratio);

        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, newWidth, newHeight, null);
        g.dispose();

        ImageIO.write(resized, "png", targetPath.toFile());
    }
}