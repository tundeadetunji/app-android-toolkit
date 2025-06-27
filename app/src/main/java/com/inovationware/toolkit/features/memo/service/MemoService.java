package com.inovationware.toolkit.features.memo.service;

import com.inovationware.toolkit.features.memo.model.Memo;

import java.io.IOException;

public interface MemoService {
    void saveNoteToCloud(Memo memo) throws IOException;
}
