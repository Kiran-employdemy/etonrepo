package com.eaton.platform.core.services.search.find.replace.tags.impl;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.text.StrSubstitutor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.eaton.platform.core.bean.packaging.PackageBean;
import com.eaton.platform.core.bean.search.find.replace.request.Mode;
import com.eaton.platform.core.bean.search.find.replace.request.TagsModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.BackupResult;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.FindAndReplaceConstants;
import com.eaton.platform.core.services.search.find.replace.FindReplaceBackupService;
import com.eaton.platform.core.services.search.find.replace.tags.FindReplaceBackupTagsService;

/**
 * The Class FindReplaceBackupTagsServiceImpl.
 *
 * Creates backups for content modified by "find and replace" functionality for tags.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = FindReplaceBackupTagsService.class,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Find Replace Backup Tags Service",
				AEMConstants.PROCESS_LABEL + "FindReplaceBackupTagsServiceImpl" })
public class FindReplaceBackupTagsServiceImpl implements FindReplaceBackupTagsService {

	private static final String DESCRIPTION_TEMPLATE = "Backup for tags ${mode} operation.\nAuthor: ${author}.\nSearch root path: ${path}.\n"
			+ "Tags to find:\n${tagsToFind}\n${modificationType}:\n${tagsForModification}";

	// max number of tag fields and tag values for one field to be included into description
	private static final int MAX_ENTRIES = 5;

	@Reference
	private FindReplaceBackupService backupService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BackupResult createBackup(final TagsModificationRequestBean modificationRequest) {
		final PackageBean packageBean = this.backupService.createBackup(FindAndReplaceConstants.TAGS_PACKAGE_NAME, this.buildDescription(modificationRequest),
				modificationRequest.getModificationPaths());

		final BackupResult result = new BackupResult();
		result.setPackagePath(packageBean.getPath());

		return result;
	}

	private String buildDescription(final TagsModificationRequestBean modificationRequest) {
		final String newSectionTitle = Mode.DELETE == modificationRequest.getMode() ? "Tags to delete" : "New tags";

		return StrSubstitutor.replace(DESCRIPTION_TEMPLATE, Map.of("mode", modificationRequest.getMode().toString().toLowerCase(Locale.getDefault()), "author",
				modificationRequest.getUserId(), "path", modificationRequest.getPath(), "tagsToFind", buildTagsString(modificationRequest.getTagsToFind()),
				"modificationType", newSectionTitle, "tagsForModification", buildTagsString(modificationRequest.getTagsForModification())));
	}

	private static String buildTagsString(final Map<String, List<String>> tagsMap) {
		final StringBuilder tags = new StringBuilder();
		int fieldCount = 1;
		final Set<Map.Entry<String, List<String>>> entrySet = tagsMap.entrySet();

		for (final Map.Entry<String, List<String>> entry : entrySet) {
			int valueCount = 1;
			tags.append(entry.getKey()).append(": ");

			final List<String> tagValues = entry.getValue();

			for (final String tagValue : tagValues) {
				tags.append(tagValue);

				if (valueCount >= MAX_ENTRIES) {
					tags.append(", ...");
					break;
				}
				if (valueCount < tagValues.size()) {
					tags.append(", ");
				}
				if (valueCount == tagValues.size()) {
					tags.append(";");
				}
				valueCount++;
			}
			if (fieldCount >= MAX_ENTRIES) {
				tags.append("\n...");
				break;
			}
			if (fieldCount < entrySet.size()) {
				tags.append("\n");
			}
			fieldCount++;
		}
		return tags.toString();
	}

}
