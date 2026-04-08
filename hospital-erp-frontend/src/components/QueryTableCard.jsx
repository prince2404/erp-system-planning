import { useQuery } from '@tanstack/react-query';
import { endpoints } from '../api/endpoints.js';
import DataTable from './DataTable.jsx';
import SectionCard from './SectionCard.jsx';

export default function QueryTableCard({ title, description, queryKey, endpoint, params, columns, empty }) {
  const query = useQuery({
    queryKey: [queryKey, params],
    queryFn: () => endpoints.get(endpoint, params),
    enabled: params?.enabled === false ? false : true
  });
  const hasData = Array.isArray(query.data)
    ? query.data.length > 0
    : Array.isArray(query.data?.content)
      ? query.data.content.length > 0
      : Boolean(query.data);

  return (
    <SectionCard title={title} description={description}>
      {query.isLoading ? <div className="h-32 animate-pulse rounded-2xl bg-slate-100" /> : <DataTable rows={query.data} columns={columns} empty={empty} />}
      {query.isError && !hasData ? <p className="mt-3 text-sm font-medium text-red-600">{query.error.response?.data?.message || query.error.message}</p> : null}
    </SectionCard>
  );
}
