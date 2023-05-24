package com.example.gonggu.ui.post;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gonggu.R;

import java.util.ArrayList;
import java.util.List;

public class MyPostsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<String> myPostsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_posts, container, false);

        recyclerView = view.findViewById(R.id.myPostsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myPostsList = getMyPosts(); // 사용자가 작성한 글 목록을 가져옴

        adapter = new RecyclerView.Adapter(myPostsList, new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {
                // 아이템 클릭 이벤트 처리
            }
        });
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<String> getMyPosts() {
        // 사용자가 작성한 글 목록을 가져와서 반환하는 메서드
        // 여기에서는 임시로 예시 데이터를 반환합니다.
        List<String> posts = new ArrayList<>();
        posts.add("내가 작성한 글 1");
        posts.add("내가 작성한 글 2");
        posts.add("내가 작성한 글 3");
        return posts;
    }
}