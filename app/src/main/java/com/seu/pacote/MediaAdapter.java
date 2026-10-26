package com.seu.pacote;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atividadeac2.R;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {
    private List<Media> mediaList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Media media);
        void onItemLongClick(Media media);
    }

    public MediaAdapter(List<Media> mediaList, OnItemClickListener listener) {
        this.mediaList = mediaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media, parent, false);
        return new MediaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        Media media = mediaList.get(position);
        holder.txtNome.setText(media.getNome());
        String detalhes = String.format("%s | %s | Ano: %d | Nota: %d | %s",
                media.getTipo(), media.getGenero(), media.getAno(), media.getNota(),
                media.isAssistido() ? "Assistido" : "Não assistido");
        holder.txtDetalhes.setText(detalhes);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(media));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(media);
            return true;
        });
    }

    @Override
    public int getItemCount() { return mediaList.size(); }

    public void updateList(List<Media> newList) {
        mediaList = newList;
        notifyDataSetChanged();
    }

    class MediaViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtDetalhes;
        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNomeItem);
            txtDetalhes = itemView.findViewById(R.id.txtDetalhesItem);
        }
    }
}
